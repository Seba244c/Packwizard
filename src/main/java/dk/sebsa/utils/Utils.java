package dk.sebsa.utils;

import org.lwjgl.PointerBuffer;
import org.lwjgl.util.nfd.NativeFileDialog;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sebs
 */
public class Utils {
    public static String[] enumStrings(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }


    public static String pickFolderDialog() {
        PointerBuffer out = PointerBuffer.allocateDirect(1);
        NativeFileDialog.NFD_PickFolder(out, (ByteBuffer) null);
        try {
            return out.getStringASCII();
        } catch (NullPointerException e) { return ""; }
    }

    public static String getRegexGroup(String s, Pattern regexp) {
        Matcher matcher = regexp.matcher(s);
        return matcher.find() ? matcher.group(1) : null;
    }
}
