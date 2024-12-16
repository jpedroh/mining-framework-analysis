package com.marginallyclever.makelangelo.makeArt;

import com.marginallyclever.makelangelo.Translator;
import com.marginallyclever.makelangelo.turtle.Turtle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InfillTurtleAction extends TurtleModifierAction {
	private static final Logger logger = LoggerFactory.getLogger(InfillTurtleAction.class);

	private static final long serialVersionUID = -8653065260609614796L;

	public InfillTurtleAction() {
		super(Translator.get("InfillTurtleAction.title"));
	}

	@Override
	public Turtle run(Turtle t) {
		InfillTurtle infill = new InfillTurtle();
		Turtle result = new Turtle(t);
		try {
			result.add(infill.run(t));
			// PPAC : to indirectly update the slider last max limit (or else max limit is not updated and no view update on infill menu item clic)
			myMakelangelo.setTurtle(t);
		} catch (java.lang.Exception ex) {
			logger.error("Failed to infill", ex);
		}
		return result;
	}
}