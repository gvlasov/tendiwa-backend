package tendiwa.geometry;

import static tendiwa.geometry.DSL.*;

public class CyrillicTemplates {
    public static final RectangleBuilderTemplate YE = new RectangleBuilderTemplate() {
        @Override
        public RectangleSystem build() {
            return builder(0)
                    .place(rectangle(3, 12), somewhere())
                    .rememberRectangle()
                    .place(rectangle(6, 3), near(REMEMBERED_RECTANGLE).fromSide(E).align(N))
                    .place(rectangle(6, 3), near(REMEMBERED_RECTANGLE).fromSide(E).inMiddle())
                    .place(rectangle(6, 3), near(REMEMBERED_RECTANGLE).fromSide(E).align(S)).done();
        }
    };
    public static final RectangleBuilderTemplate EL = new RectangleBuilderTemplate() {
        @Override
        public RectangleSystem build() {
            return builder(0)
                    .place(rectangle(3, 3), somewhere())
                    .place(rectangle(3, 9), near(FIRST_RECTANGLE).fromCorner(SW))
                    .place(rectangle(3, 9), near(FIRST_RECTANGLE).fromCorner(SE)).done();
        }
    };
    public static final RectangleBuilderTemplate I = new RectangleBuilderTemplate() {

        @Override
        public RectangleSystem build() {
            return builder(0)
                    .place(rectangle(3, 12), somewhere())
                    .place(rectangle(3, 3), near(FIRST_RECTANGLE).fromSide(E).align(S).shift(-3))
                    .place(rectangle(3, 3), near(LAST_RECTANGLE).fromCorner(NE))
                    .place(rectangle(3, 12), awayFrom(FIRST_RECTANGLE).fromSide(E).margin(6).align(N))
                    .done();
        }
    };
    public static final RectangleBuilderTemplate TE = new RectangleBuilderTemplate() {
        @Override
        public RectangleSystem build() {
            return builder(0)
                    .place(rectangle(12, 3), somewhere())
                    .place(rectangle(3, 9), near(FIRST_RECTANGLE).fromSide(S).inMiddle()).done();
        }
    };
    public static final RectangleBuilderTemplate YERY = new RectangleBuilderTemplate() {
        @Override
        public RectangleSystem build() {
            return builder(0)
                    .place(rectangle(3, 12), somewhere())
                    .place(rectangle(6, 3), near(FIRST_RECTANGLE).fromSide(E).align(S).shift(-6))
                    .place(rectangle(6, 3), near(FIRST_RECTANGLE).fromSide(E).align(S))
                    .place(rectangle(3, 3), awayFrom(FIRST_RECTANGLE).fromSide(E).margin(3).align(S).shift(-3))
                    .place(rectangle(3, 12), awayFrom(FIRST_RECTANGLE).fromSide(E).margin(9).align(S))
                    .done();


        }
    };
    public static final RectangleBuilderTemplate U = new RectangleBuilderTemplate() {
        @Override
        public RectangleSystem build() {
            return builder(0)
                    .place(rectangle(3, 3), somewhere())
                    .place(rectangle(3, 3), near(FIRST_RECTANGLE).fromCorner(SE))
                    .place(rectangle(3, 3), near(LAST_RECTANGLE).fromCorner(SE))
                    .rememberRectangle()
                    .place(rectangle(3, 3), near(LAST_RECTANGLE).fromCorner(NE))
                    .place(rectangle(3, 3), near(LAST_RECTANGLE).fromCorner(NE))
                    .place(rectangle(3, 3), near(REMEMBERED_RECTANGLE).fromCorner(SW))
                    .done();
        }
    };
    public static final RectangleBuilderTemplate ZE = new RectangleBuilderTemplate() {
        @Override
        public RectangleSystem build() {
            return builder(0)
                    .place(rectangle(6, 3), somewhere())
                    .place(rectangle(3,3), near(LAST_RECTANGLE).fromCorner(SE))
                    .place(rectangle(6, 3), near(LAST_RECTANGLE).fromCorner(SW))
                    .place(rectangle(3,3), near(LAST_RECTANGLE).fromCorner(SE))
                    .place(rectangle(6,3), near(LAST_RECTANGLE).fromCorner(SW))
                    .done();
        }
    };
    public static final RectangleBuilderTemplate O = new RectangleBuilderTemplate() {
        @Override
        public RectangleSystem build() {
            return builder(0)
                    .place(rectangle(6, 3), somewhere())
                    .place(rectangle(3, 6), near(FIRST_RECTANGLE).fromCorner(SW))
                    .place(rectangle(3, 6), near(FIRST_RECTANGLE).fromCorner(SE))
                    .place(rectangle(6, 3), near(LAST_RECTANGLE).fromCorner(SW))
                    .done();
        }
    };
    public static final RectangleBuilderTemplate ER = new RectangleBuilderTemplate() {
        @Override
        public RectangleSystem build() {
            return builder(0)
                    .place(rectangle(9, 3), somewhere())
                    .place(rectangle(3, 9), near(LAST_RECTANGLE).fromSide(S).align(W))
                    .place(rectangle(3, 6), near(FIRST_RECTANGLE).fromSide(S).align(E))
                    .place(rectangle(3, 3), near(LAST_RECTANGLE).fromSide(W).align(S))
                    .done();
        }
    };

}
