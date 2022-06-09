package MainApp;

public class FieldRect<T> {
    private T width, height, X, Y;

    public void setHeight(T height) {
        this.height = height;
    }

    public void setWidth(T width) {
        this.width = width;
    }

    public T getHeight() {
        return height;
    }

    public T getWidth() {
        return width;
    }

    public T getX() {
        return X;
    }

    public T getY() {
        return Y;
    }

    public void setX(T x) {
        X = x;
    }

    public void setY(T y) {
        Y = y;

    }


}

class Position {
    public double x, y;

    Position(double x, double y) {
        this.x = x;
        this.y = y;
    }
}

final class Positions {
    public static Position R2A(Position originalSize, Position scaledSize, Position relativePointPosition) {
        return new Position(((originalSize.x / scaledSize.x) * relativePointPosition.x),
                ((originalSize.y / scaledSize.y) * relativePointPosition.y));
    }

    public static Position A2R(Position originalSize, Position scaledSize, Position absolutePointPosition) {
        return new Position((absolutePointPosition.x / (originalSize.x / scaledSize.x)),
                (absolutePointPosition.y / (originalSize.y / scaledSize.y)));
    }
}
