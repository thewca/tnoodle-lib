package org.worldcubeassociation.tnoodle.svglite;

import java.util.ArrayList;
import java.util.List;

public class Path extends Element {

    static class Command {
        int type;
        double[] coords;
        public Command(int type, double[] coords) {
            this.type = type;
            this.coords = coords;
        }
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(PathIterator.SVG_LANGUAGE_COMMANDS.charAt(type));
            for(int i = 0; coords != null && i < coords.length; i++) {
                sb.append(" ");
                sb.append(coords[i]);
            }
            return sb.toString();
        }
    }

    protected List<Command> commands = null;

    public Path() {
        super("path");
    }

    public Path(Path p) {
        super(p);
        if(p.commands != null) {
            this.commands = new ArrayList<>(p.commands);
        }
    }

    public PathIterator getPathIterator() {
        return new PathIterator(this);
    }

    public void moveTo(double x, double y) {
        if(commands == null) {
            commands = new ArrayList<>();
        }

        int type = PathIterator.SEG_MOVETO;
        double[] coords = new double[] { x, y };
        commands.add(new Command(type, coords));
    }

    private void azzertMoveTo() {
        assert commands != null : "First command must be moveTo";
    }

    public void lineTo(double x, double y) {
        azzertMoveTo();

        int type = PathIterator.SEG_LINETO;
        double[] coords = new double[] { x, y };
        commands.add(new Command(type, coords));
    }

    public void closePath() {
        azzertMoveTo();

        Command closeCommand = new Command(PathIterator.SEG_CLOSE, null);
        commands.add(closeCommand);
    }

    public void translate(double x, double y) {
        for(Command c : commands) {
            switch(c.type) {
                case PathIterator.SEG_MOVETO:
                case PathIterator.SEG_LINETO:
                    c.coords[0] += x;
                    c.coords[1] += y;
                    break;
                case PathIterator.SEG_CLOSE:
                    break;
                default:
                    assert false;
            }
        }
    }

    public String getD() {
        StringBuilder sb = new StringBuilder();
        for(Command c : commands) {
            sb.append(" ").append(c.toString());
        }
        if(sb.length() == 0) {
            return "";
        }
        return sb.substring(1);
    }

    public void buildString(StringBuilder sb, int level) {
        // We're about to get dumped to a string, lets update
        // our "d" attribute first.
        setAttribute("d", getD());
        super.buildString(sb, level);
    }
}
