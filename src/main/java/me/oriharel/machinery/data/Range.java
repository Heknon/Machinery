package me.oriharel.machinery.data;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Range {
    private final int start;
    private final int end;

    public Range(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int random() {
        return ThreadLocalRandom.current().nextInt(start, end + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Range range = (Range) o;
        return start == range.start &&
                end == range.end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
