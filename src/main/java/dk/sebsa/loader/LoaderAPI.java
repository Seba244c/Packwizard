package dk.sebsa.loader;

import dk.sebsa.enums.ModLoaders;

import java.util.Objects;

/**
 * @author sebs
 */
public interface LoaderAPI {
    String[] loaderVersions();

    static LoaderAPI getApi(ModLoaders loader) {
        if (Objects.requireNonNull(loader) == ModLoaders.Quilt) {
            return QuiltAPI.instance;
        }
        return null;
    }
}
