package tendiwa.core.meta;

public class Range {
public final int min;
public final int max;
public Range(int min, int max) {
	this.min = min;
	this.max = max;
}
public String toString() {
	return "["+min+","+max+"]";
}
/**
 * Returns a new Range that is an intersection of this Range and another one.
 * @param b
 * 		Range to be intersected with.
 * @return
 */
public Range intersection(Range b) {
	if (b.max < min) {
		return null;
	}
	if (b.min > max) {
		return null;
	}
	if (min >= b.min && max <= b.max) {
		return new Range(min, max);
	}
	if (b.min >= min && b.max <= max) {
		return new Range(b.min, b.max);
	}
	if (min >= b.max) {
		return new Range(min, b.max);
	} else {
		return new Range(b.min, max);
	}
}
@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + max;
	result = prime * result + min;
	return result;
}
@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	Range other = (Range) obj;
	if (max != other.max)
		return false;
	if (min != other.min)
		return false;
	return true;
}

}