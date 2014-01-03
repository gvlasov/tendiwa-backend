package org.tendiwa.groovy

import tendiwa.core.CharacterType

class DSL {

    def static characterType(closure) {
        CharacterType type = new CharacterType()
        closure.delegate = type;
        closure();
        characters[type.name] = type;
        return type;
    }
    def static characters = new Registry<CharacterType>();
}

