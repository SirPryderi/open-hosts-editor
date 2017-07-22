package me.vittorio_io.openhostseditor;

import org.junit.Test;

import me.vittorio_io.openhostseditor.model.HostRule;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void hostRuleEqualsTEst() throws Exception{
        HostRule rule1 = HostRule.fromHostLine("0.0.0.0 localhost");
        HostRule rule1bis = HostRule.fromHostLine("0.0.0.0 localhost");
        HostRule rule2 = HostRule.fromHostLine("127.0.0.1 localhost");
        HostRule rule3 = HostRule.fromHostLine("0.0.0.0 website");

        assertEquals(rule1, rule1);

        assertNotEquals(rule1, rule2);

        assertNotEquals(rule2, rule3);

        assertNotEquals(rule1, rule3);

        assertEquals(rule1, rule1bis);
    }
}