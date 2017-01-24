package epos.ui.util;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.io.IOException;
import java.io.Serializable;

public class SerializedBasicStroke implements Serializable {
	Stroke stroke;

	public SerializedBasicStroke() {

	}

	public SerializedBasicStroke(Stroke s) {
		this.stroke = s;
	}

	private void writeObject(java.io.ObjectOutputStream stream)
			throws IOException {
		if (stroke != null) {
			stream.writeBoolean(false);
			if (stroke instanceof BasicStroke) {
				BasicStroke s = (BasicStroke) stroke;
				stream.writeObject(BasicStroke.class);
				stream.writeFloat(s.getLineWidth());
				stream.writeInt(s.getEndCap());
				stream.writeInt(s.getLineJoin());
				stream.writeFloat(s.getMiterLimit());
				stream.writeObject(s.getDashArray());
				stream.writeFloat(s.getDashPhase());
			} else {
				stream.writeObject(stroke.getClass());
				stream.writeObject(stroke);
			}
		} else {
			stream.writeBoolean(true);
		}
	}

	private void readObject(java.io.ObjectInputStream stream)
			throws IOException, ClassNotFoundException {
		Stroke result = null;
		boolean isNull = stream.readBoolean();
		if (!isNull) {
			Class c = (Class) stream.readObject();
			if (c.equals(BasicStroke.class)) {
				float width = stream.readFloat();
				int cap = stream.readInt();
				int join = stream.readInt();
				float miterLimit = stream.readFloat();
				float[] dash = (float[]) stream.readObject();
				float dashPhase = stream.readFloat();
				result = new BasicStroke(width, cap, join, miterLimit, dash,
						dashPhase);
			} else {
				result = (Stroke) stream.readObject();
			}
		}
		this.stroke = result;
	}

	public Stroke getStroke() {
		return stroke;
	}

	public void setStroke(Stroke stroke) {
		this.stroke = stroke;
	}
}
