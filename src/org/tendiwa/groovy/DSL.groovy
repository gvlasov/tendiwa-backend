package org.tendiwa.groovy

import tendiwa.core.*

class DSL {

    def static characters = new Registry<CharacterType>();
    def static characterAbilities = new Registry<CharacterAbility>()
    def static soundTypes = new Registry<SoundType>()
    def static spells = new Registry<Spell>()
    def static objectTypes = new Registry<ObjectType>()
    def static floorTypes = new Registry<FloorType>()
    def static wallTypes = new Registry<WallType>()
    def static materials = new Registry<Material>()
    def static itemTypes = new Registry<ItemType>()
    def static ammunitionTypes = new Registry<AmmunitionType>()

    def static newCharacterType(closure) {
        CharacterType type = new CharacterType()
        closure.delegate = type;
        closure();
        characters[type.name] = type;
        return type;
    }

    def static newCharacterAbility(closure) {
        CharacterAbility ability = new CharacterAbility() {}
        closure.delegate = ability
        closure()
        characterAbilities[ability.name] = ability
        return ability;
    }

    def static newSoundType(closure) {
        SoundType sound = new SoundType() {}
        closure.delegate = sound
        closure()
        soundTypes[sound.name] = sound
        return sound;
    }

    def static newSpell(closure) {
        Spell spell = new Spell() {}
        closure.delegate = spell
        closure()
        spells[spell.name] = spell
        return spell;
    }

    def static newObjectType(closure) {
        ObjectType type = new ObjectType() {}
        closure.delegate = type
        closure()
        objectTypes[type.name] = type
        return type;
    }

    def static newFloorType(closure) {
        FloorType type = new FloorType() {}
        closure.delegate = type
        closure()
        floorTypes[type.name] = type
        return type;
    }

    def static newWallType(closure) {
        WallType type = new WallType() {}
        closure.delegate = type
        closure()
        wallTypes[type.name] = type
        return type;
    }
    def static newMaterial(closure) {
        Material type = new Material() {}
        closure.delegate = type
        closure()
        materials[type.name] = type
        return type;
    }
    def static newItemType(closure) {
        ItemType type = new ItemType() {}
        closure.delegate = type
        closure()
        itemTypes[type.name] = type
        return type;
    }
    def static newAmmunitionType(closure) {
        AmmunitionType type = new AmmunitionType() {}
        closure.delegate = type
        closure()
        ammunitionTypes[type.name] = type
        return type;
    }
}

