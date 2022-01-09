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
                "luna", "루나" -> return LUNA
                "scania", "스카니아" -> return SCANIA
                "elysium", "엘리시움" -> return ELYSIUM
                "reboot", "리부트" -> return REBOOT
                "croa", "크로아" -> return CROA
                "aurora", "오로라" -> return AURORA
                "bera", "베라" -> return BERA
                "red", "레드" -> return RED
                "union", "유니온" -> return UNION
                "zenith", "제니스" -> return ZENITH
                "enosis", "이노시스" -> return ENOSIS
                "reboot2", "리부트2" -> return REBOOT2
                "arcane", "아케인" -> return ARCANE
                "nova", "노바" -> return NOVA
                else -> return NONE
            }
        }
    }
}