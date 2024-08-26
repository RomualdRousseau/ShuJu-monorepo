package com.github.romualdrousseau.shuju;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.IntStream;

import org.junit.Test;

import com.github.romualdrousseau.shuju.bigdata.ChunkSerializer;
import com.github.romualdrousseau.shuju.bigdata.ChunkSerializerFactory;
import com.github.romualdrousseau.shuju.bigdata.ChunkSerializerFactory.SerializerType;
import com.github.romualdrousseau.shuju.bigdata.DataFrameWriter;
import com.github.romualdrousseau.shuju.bigdata.Row;

public class Test_BigData1 {

    private final ChunkSerializer serializer = ChunkSerializerFactory.newInstance(SerializerType.FURY);

    @Test
    public void testSerialize() throws IOException {
        final var rows = IntStream.range(0, 10000)
                .mapToObj(i -> Row.of(IntStream.range(0, 1000)
                        .mapToObj(j -> "nisl purus in mollis nunc")
                        .toArray(String[]::new)))
                .toArray(Row[]::new);
        final var bytes = serializer.serialize(rows);
        System.out.println(bytes.length >> 20);

        final var rows2 = serializer.deserialize(bytes);
        assertEquals(rows.length, rows2.length);
        Arrays.stream(rows2).forEach(row -> {
            row.forEach(x -> {
                assertEquals("nisl purus in mollis nunc", x);
            });
        });
    }

    @Test
    public void testDataFrameWhole() throws IOException {
        try (final var writer = new DataFrameWriter(100, 10)) {
            for (int i = 0; i < 10; i++) {
                writer.write(Row.of(IntStream.range(0, writer.getColumnCount())
                        .mapToObj(j -> "nisl purus in mollis nunc")
                        .toArray(String[]::new)));
            }
            try (final var df = writer.getDataFrame()) {
                df.forEach(y -> {
                    y.forEach(x -> {
                        assertEquals("nisl purus in mollis nunc", x);
                    });
                });
            }
        }
    }

    @Test
    public void testDataFrameFullView() throws IOException {
        try (final var writer = new DataFrameWriter(100, 10)) {
            for (int i = 0; i < 10; i++) {
                writer.write(Row.of(IntStream.range(0, writer.getColumnCount())
                        .mapToObj(j -> "nisl purus in mollis nunc")
                        .toArray(String[]::new)));
            }
            try (final var df = writer.getDataFrame()) {
                final var view = df.view(0, 0, 10, 10);
                view.forEach(y -> {
                    y.forEach(x -> {
                        assertEquals("nisl purus in mollis nunc", x);
                    });
                });
            }
        }
    }

    @Test
    public void testDataFramePartialView() throws IOException {
        try (final var writer = new DataFrameWriter(100, 100)) {
            for (int i = 0; i < 5000; i++) {
                writer.write(Row.of(IntStream.range(0, writer.getColumnCount())
                        .mapToObj(j -> "nisl purus in mollis nunc")
                        .toArray(String[]::new)));
            }
            try (final var df = writer.getDataFrame()) {
                final var view = df.view(2000, 50, 10, 10);
                view.forEach(y -> {
                    y.forEach(x -> {
                        assertEquals("nisl purus in mollis nunc", x);
                    });
                });
            }
        }
    }

    @Test
    public void testDataFrameRandom() throws IOException {
        try (final var writer = new DataFrameWriter(100, 100)) {
            for (int i = 0; i < 5000; i++) {
                writer.write(Row.of(IntStream.range(0, writer.getColumnCount())
                        .mapToObj(j -> "nisl purus in mollis nunc")
                        .toArray(String[]::new)));
            }
            try (final var df = writer.getDataFrame()) {
                assertEquals("nisl purus in mollis nunc", df.getCell(0, 5));
                assertEquals("nisl purus in mollis nunc", df.getCell(5, 8));
            }
        }
    }
}
