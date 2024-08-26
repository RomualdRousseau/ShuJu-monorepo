package com.github.romualdrousseau.shuju;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;

import com.github.romualdrousseau.shuju.bigdata.DataFrameWriter;
import com.github.romualdrousseau.shuju.bigdata.Row;

public class Test_BigData2 {

    @Test
    public void testDataFrameMassive() throws IOException {
        if (!Path.of("/mnt/media").toFile().exists()) {
            return;
        }
        try (final var writer = new DataFrameWriter(10000, 1000)) {
            for (int i = 0; i < 10000000; i++) {
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
    public void testArrayListMassive() {
        if (!Path.of("/mnt/media").toFile().exists()) {
            return;
        }
        assertThrows(OutOfMemoryError.class, () -> {
            final var list = new ArrayList<String[]>();
            for (int i = 0; i < 10000000; i++) {
                list.add(IntStream.range(0, 1000)
                        .mapToObj(j -> "nisl purus in mollis nunc")
                        .toArray(String[]::new));
            }
            list.forEach(y -> {
                List.of(y).forEach(x -> {
                    assertEquals("nisl purus in mollis nunc", x);
                });
            });
        });
    }
}
