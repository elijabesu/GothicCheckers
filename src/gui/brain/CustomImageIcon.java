package gui.brain;

import javax.swing.*;
import java.awt.*;

public class CustomImageIcon extends ImageIcon {
    private boolean isWhite;
    private boolean isKing;

    public CustomImageIcon(Image image, boolean isWhite, boolean isKing) {
        super(image);
        this.isKing = isKing;
        this.isWhite = isWhite;
    }

    public boolean isKing() {
        return isKing;
    }

    public boolean isWhite() {
        return isWhite;
    }
}
