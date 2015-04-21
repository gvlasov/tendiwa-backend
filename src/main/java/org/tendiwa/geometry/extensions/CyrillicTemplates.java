package org.tendiwa.geometry.extensions;

import org.tendiwa.geometry.RectangleBuilderTemplate;

import static org.tendiwa.geometry.DSL.*;
import static org.tendiwa.geometry.RectanglePointer.*;

public class CyrillicTemplates {
	public static final RectangleBuilderTemplate YE =
		new RectangleBuilderTemplate(
			() -> builder(0)
				.place(rectangle(3, 12), somewhere())
				.place(rectangle(6, 3), near(first).fromSide(E).align(N))
				.place(rectangle(6, 3), near(first).fromSide(E).inMiddle())
				.place(rectangle(6, 3), near(first).fromSide(E).align(S)).done()
		);
	public static final RectangleBuilderTemplate EL = new RectangleBuilderTemplate(

		() -> builder(0)
			.place(rectangle(3, 3), somewhere())
			.place(rectangle(3, 9), near(first).fromCorner(SW))
			.place(rectangle(3, 9), near(first).fromCorner(SE)).done()
	);
	public static final RectangleBuilderTemplate I =
		new RectangleBuilderTemplate(
			() -> builder(0)
				.place(rectangle(3, 12), somewhere())
				.place(rectangle(3, 3), near(first).fromSide(E).align(S).shift(-3))
				.call("second")
				.place(rectangle(3, 3), near(named("second")).fromCorner(NE))
				.place(rectangle(3, 12), awayFrom(first).fromSide(E).margin(6).align(N))
				.done()
		);

	public static final RectangleBuilderTemplate TE =
		new RectangleBuilderTemplate(

			() -> builder(0)
				.place(rectangle(12, 3), somewhere())
				.place(rectangle(3, 9), near(first).fromSide(S).inMiddle()).done()
		);

	public static final RectangleBuilderTemplate YERY =
		new RectangleBuilderTemplate(
			() -> builder(0)
				.place(rectangle(3, 12), somewhere())
				.place(rectangle(6, 3), near(first).fromSide(E).align(S).shift(-6))
				.place(rectangle(6, 3), near(first).fromSide(E).align(S))
				.place(rectangle(3, 3), awayFrom(first).fromSide(E).margin(3).align(S).shift(-3))
				.place(rectangle(3, 12), awayFrom(first).fromSide(E).margin(9).align(S))
				.done()

		);
	public static final RectangleBuilderTemplate U =
		new RectangleBuilderTemplate(
			() -> builder(0)
				.place(rectangle(3, 3), somewhere())
				.place(rectangle(3, 3), near(previous).fromCorner(SE))
				.place(rectangle(3, 3), near(previous).fromCorner(SE))
				.call("r")
				.place(rectangle(3, 3), near(previous).fromCorner(NE))
				.place(rectangle(3, 3), near(previous).fromCorner(NE))
				.place(rectangle(3, 3), near(named("r")).fromCorner(SW))
				.done()

		);
	public static final RectangleBuilderTemplate ZE =
		new RectangleBuilderTemplate(
			() -> builder(0)
				.place(rectangle(6, 3), somewhere())
				.place(rectangle(3, 3), near(previous).fromCorner(SE))
				.place(rectangle(6, 3), near(previous).fromCorner(SW))
				.place(rectangle(3, 3), near(previous).fromCorner(SE))
				.place(rectangle(6, 3), near(previous).fromCorner(SW))
				.done()

		);
	public static final RectangleBuilderTemplate O =
		new RectangleBuilderTemplate(
			() -> builder(0)
				.place(rectangle(6, 3), somewhere())
				.place(rectangle(3, 6), near(first).fromCorner(SW))
				.place(rectangle(3, 6), near(first).fromCorner(SE))
				.place(rectangle(6, 3), near(previous).fromCorner(SW))
				.done()

		);
	public static final RectangleBuilderTemplate ER =
		new RectangleBuilderTemplate(
			() -> builder(0)
				.place(rectangle(9, 3), somewhere())
				.place(rectangle(3, 9), near(first).fromSide(S).align(W))
				.place(rectangle(3, 6), near(first).fromSide(S).align(E))
				.place(rectangle(3, 3), near(previous).fromSide(W).align(S))
				.done()
		);

}
