package mapleGBP.model

import com.fasterxml.jackson.annotation.JsonValue

enum class World(
    @JsonValue
    val worldName: String
) {
    LUNA("luna"),
    SCANIA("scania"),
    ELYSIUM("elysium"),
    REBOOT("reboot"),
    CROA("croa"),
    AURORA("aurora"),
    BERA("bera"),
    RED("red"),
    UNION("union"),
    ZENITH("zenith"),
    ENOSIS("enosis"),
    REBOOT2("reboot2"),
    ARCANE("arcane"),
    NOVA("nova"),
    NONE("none");

    companion object {
        fun from(worldName: String): World {
            when (worldName) {
                "luna" -> return LUNA
                "scania" -> return SCANIA
                "elysium" -> return ELYSIUM
                "reboot" -> return REBOOT
                "croa" -> return CROA
                "aurora" -> return AURORA
                "bera" -> return BERA
                "red" -> return RED
                "union" -> return UNION
                "zenith" -> return ZENITH
                "enosis" -> return ENOSIS
                "reboot2" -> return REBOOT2
                "arcane" -> return ARCANE
                "nova" -> return NOVA
                else -> return NONE
            }
        }
    }
}