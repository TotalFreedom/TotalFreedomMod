package ca.momothereal.mojangson;

import ca.momothereal.mojangson.ex.MojangsonParseException;
import ca.momothereal.mojangson.value.*;

import static ca.momothereal.mojangson.MojangsonToken.*;

public class MojangsonFinder {

    /**
     * Automatically detects the appropriate MojangsonValue from the given value.
     * @param value The value to parse
     * @return The resulting MojangsonValue. If the type couldn't be found, it falls back to MojangsonString
     * @throws MojangsonParseException if the given value could not be parsed
     */
    public static MojangsonValue readFromValue(String value) throws MojangsonParseException {
        MojangsonValue val = new MojangsonString();
        val.read(value);
        return val;
    }
}
