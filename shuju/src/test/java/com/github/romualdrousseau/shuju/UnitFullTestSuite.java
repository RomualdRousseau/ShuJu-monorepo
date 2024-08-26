package com.github.romualdrousseau.shuju;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        Test_Commons.class,
        Test_Redux.class,
        Test_Commons.class,
        Test_RegexComparer.class,
        Test_StringUtils.class,
        Test_Tensor.class,
        Test_Text.class,
        Test_BigData1.class,
        Test_BigData2.class
})

public class UnitFullTestSuite {
}
