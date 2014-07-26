package org.tendiwa.groovy

import org.tendiwa.core.AmmunitionType
import org.tendiwa.core.BorderObjectType
import org.tendiwa.core.CharacterAbility
import org.tendiwa.core.CharacterType
import org.tendiwa.core.FloorType
import org.tendiwa.core.ItemType
import org.tendiwa.core.Material
import org.tendiwa.core.ObjectType
import org.tendiwa.core.SoundType
import org.tendiwa.core.Spell
import org.tendiwa.core.WallType
import tendiwa.core.*

class DSL {

    def static characters = Registry.characters;
    def static characterAbilities = Registry.characterAbilities;
    def static soundTypes = Registry.soundTypes
    def static spells = Registry.spells
    def static objectTypes = Registry.objectTypes
    def static floorTypes = Registry.floorTypes
    def static wallTypes = Registry.wallTypes
    def static materials = Registry.materials
    def static itemTypes = Registry.itemTypes
    def static ammunitionTypes = Registry.ammunitionTypes
    def static borderObjectTypes = Registry.borderObjectTypes

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

    def static newBorderObjectType(closure) {
        BorderObjectType type = new BorderObjectType() {}
        closure.delegate = type
        closure()
        borderObjectTypes[type.name] = type
        return type;
    }
}

