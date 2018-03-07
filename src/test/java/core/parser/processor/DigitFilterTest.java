package core.parser.processor;

import core.parser.processor.string.DigitFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DigitFilterTest {
    private DigitFilter digitFilter;

    @BeforeEach
    public void setup() {
        digitFilter = new DigitFilter();
    }

    @Test
    public void shouldFilterDigits() {
        String out = digitFilter.process("456");

        assertEquals("", out);
    }

    @Test
    public void shouldAllowAlphanumeric() {
        String out = digitFilter.process("12ABC345");

        assertEquals("12ABC345", out);
    }

    @Test
    public void shouldFilterPunctuatedDigits() {
        String out = digitFilter.process("1,234.56");

        assertEquals("", out);
    }
}
