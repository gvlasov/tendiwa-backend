package org.tendiwa.core;

public class CyrillicTemplates {
    public static final RectangleBuilderTemplate YE = new RectangleBuilderTemplate() {
        @Override
        public RectangleSystem build() {
            return DSL.builder(0)
                    .place(DSL.rectangle(3, 12), DSL.somewhere())
                    .rememberRectangle()
                    .place(DSL.rectangle(6, 3), DSL.near(DSL.REMEMBERED_RECTANGLE).fromSide(DSL.E).align(DSL.N))
                    .place(DSL.rectangle(6, 3), DSL.near(DSL.REMEMBERED_RECTANGLE).fromSide(DSL.E).inMiddle())
                    .place(DSL.rectangle(6, 3), DSL.near(DSL.REMEMBERED_RECTANGLE).fromSide(DSL.E).align(DSL.S)).done();
        }
    };
    public static final RectangleBuilderTemplate EL = new RectangleBuilderTemplate() {
        @Override
        public RectangleSystem build() {
            return DSL.builder(0)
                    .place(DSL.rectangle(3, 3), DSL.somewhere())
                    .place(DSL.rectangle(3, 9), DSL.near(DSL.FIRST_RECTANGLE).fromCorner(DSL.SW))
                    .place(DSL.rectangle(3, 9), DSL.near(DSL.FIRST_RECTANGLE).fromCorner(DSL.SE)).done();
        }
    };
    public static final RectangleBuilderTemplate I = new RectangleBuilderTemplate() {

        @Override
        public RectangleSystem build() {
            return DSL.builder(0)
                    .place(DSL.rectangle(3, 12), DSL.somewhere())
                    .place(DSL.rectangle(3, 3), DSL.near(DSL.FIRST_RECTANGLE).fromSide(DSL.E).align(DSL.S).shift(-3))
                    .place(DSL.rectangle(3, 3), DSL.near(DSL.LAST_RECTANGLE).fromCorner(DSL.NE))
                    .place(DSL.rectangle(3, 12), DSL.awayFrom(DSL.FIRST_RECTANGLE).fromSide(DSL.E).margin(6).align(DSL.N))
                    .done();
        }
    };
    public static final RectangleBuilderTemplate TE = new RectangleBuilderTemplate() {
        @Override
        public RectangleSystem build() {
            return DSL.builder(0)
                    .place(DSL.rectangle(12, 3), DSL.somewhere())
                    .place(DSL.rectangle(3, 9), DSL.near(DSL.FIRST_RECTANGLE).fromSide(DSL.S).inMiddle()).done();
        }
    };
    public static final RectangleBuilderTemplate YERY = new RectangleBuilderTemplate() {
        @Override
        public RectangleSystem build() {
            return DSL.builder(0)
                    .place(DSL.rectangle(3, 12), DSL.somewhere())
                    .place(DSL.rectangle(6, 3), DSL.near(DSL.FIRST_RECTANGLE).fromSide(DSL.E).align(DSL.S).shift(-6))
                    .place(DSL.rectangle(6, 3), DSL.near(DSL.FIRST_RECTANGLE).fromSide(DSL.E).align(DSL.S))
                    .place(DSL.rectangle(3, 3), DSL.awayFrom(DSL.FIRST_RECTANGLE).fromSide(DSL.E).margin(3).align(DSL.S).shift(-3))
                    .place(DSL.rectangle(3, 12), DSL.awayFrom(DSL.FIRST_RECTANGLE).fromSide(DSL.E).margin(9).align(DSL.S))
                    .done();


        }
    };
    public static final RectangleBuilderTemplate U = new RectangleBuilderTemplate() {
        @Override
        public RectangleSystem build() {
            return DSL.builder(0)
                    .place(DSL.rectangle(3, 3), DSL.somewhere())
                    .place(DSL.rectangle(3, 3), DSL.near(DSL.FIRST_RECTANGLE).fromCorner(DSL.SE))
                    .place(DSL.rectangle(3, 3), DSL.near(DSL.LAST_RECTANGLE).fromCorner(DSL.SE))
                    .rememberRectangle()
                    .place(DSL.rectangle(3, 3), DSL.near(DSL.LAST_RECTANGLE).fromCorner(DSL.NE))
                    .place(DSL.rectangle(3, 3), DSL.near(DSL.LAST_RECTANGLE).fromCorner(DSL.NE))
                    .place(DSL.rectangle(3, 3), DSL.near(DSL.REMEMBERED_RECTANGLE).fromCorner(DSL.SW))
                    .done();
        }
    };
    public static final RectangleBuilderTemplate ZE = new RectangleBuilderTemplate() {
        @Override
        public RectangleSystem build() {
            return DSL.builder(0)
                    .place(DSL.rectangle(6, 3), DSL.somewhere())
                    .place(DSL.rectangle(3, 3), DSL.near(DSL.LAST_RECTANGLE).fromCorner(DSL.SE))
                    .place(DSL.rectangle(6, 3), DSL.near(DSL.LAST_RECTANGLE).fromCorner(DSL.SW))
                    .place(DSL.rectangle(3, 3), DSL.near(DSL.LAST_RECTANGLE).fromCorner(DSL.SE))
                    .place(DSL.rectangle(6, 3), DSL.near(DSL.LAST_RECTANGLE).fromCorner(DSL.SW))
                    .done();
        }
    };
    public static final RectangleBuilderTemplate O = new RectangleBuilderTemplate() {
        @Override
        public RectangleSystem build() {
            return DSL.builder(0)
                    .place(DSL.rectangle(6, 3), DSL.somewhere())
                    .place(DSL.rectangle(3, 6), DSL.near(DSL.FIRST_RECTANGLE).fromCorner(DSL.SW))
                    .place(DSL.rectangle(3, 6), DSL.near(DSL.FIRST_RECTANGLE).fromCorner(DSL.SE))
                    .place(DSL.rectangle(6, 3), DSL.near(DSL.LAST_RECTANGLE).fromCorner(DSL.SW))
                    .done();
        }
    };
    public static final RectangleBuilderTemplate ER = new RectangleBuilderTemplate() {
        @Override
        public RectangleSystem build() {
            return DSL.builder(0)
                    .place(DSL.rectangle(9, 3), DSL.somewhere())
                    .place(DSL.rectangle(3, 9), DSL.near(DSL.LAST_RECTANGLE).fromSide(DSL.S).align(DSL.W))
                    .place(DSL.rectangle(3, 6), DSL.near(DSL.FIRST_RECTANGLE).fromSide(DSL.S).align(DSL.E))
                    .place(DSL.rectangle(3, 3), DSL.near(DSL.LAST_RECTANGLE).fromSide(DSL.W).align(DSL.S))
                    .done();
        }
    };

}
