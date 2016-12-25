package com.jomofisher.tests.cmake.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jomofisher.cmake.serverv1.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jomof on 12/24/2016.
 */
public class AndroidGradleBuild {
    public String buildFiles[];
    public String cleanCommands[];
    public String cppFileExtensions[];
    public Map<String, Library> libraries;
    public Map<String, Toolchain> toolchains;

    private static String[] addDistinct(String original[], String add) {
        if (original == null) {
            return new String[]{add};
        }
        Set<String> set = Sets.newHashSet(original);
        set.add(add);
        return set.toArray(new String[set.size()]);
    }

    private static String takeOnly(String array[]) {
        if (array.length != 1) {
            throw new RuntimeException("Expected exactly one");
        }
        return array[0];
    }

    public static AndroidGradleBuild of(
            CodeModel codemodel,
            String cmakeExecutable,
            String abi) {
        AndroidGradleBuild result = new AndroidGradleBuild();
        result.libraries = new HashMap<>();
        for (Configuration configuration : codemodel.configurations) {
            for (Project project : configuration.projects) {
                result.cleanCommands = addDistinct(result.cleanCommands,
                        String.format("%s --build %s --target clean", cmakeExecutable, project.sourceDirectory));
                result.buildFiles = addDistinct(result.buildFiles,
                        String.format("%s/CMakeLists.txt", project.sourceDirectory));
                for (Target target : project.targets) {
                    String targetName = target.name + "-" + configuration.name + "-" + abi;
                    Library library = new Library();
                    library.abi = abi;
                    library.artifactName = target.name;
                    library.buildCommand = String.format("%s --build %s --target %s", cmakeExecutable,
                            project.sourceDirectory, target.name);
                    library.buildType = configuration.name;
                    library.output = takeOnly(target.artifacts);

                    List<FileDescription> files = Lists.newArrayList();

                    for (FileGroup fileGroup : target.fileGroups) {
                        for (String source : fileGroup.sources) {
                            FileDescription sourceDescription = new FileDescription();
                            sourceDescription.src = source;
                            sourceDescription.flags = fileGroup.compileFlags;
                            sourceDescription.workingDirectory = target.buildDirectory;
                            files.add(sourceDescription);
                        }
                    }

                    library.files = files.toArray(new FileDescription[files.size()]);
                    result.libraries.put(targetName, library);
                }
            }
        }
        return result;
    }
}
