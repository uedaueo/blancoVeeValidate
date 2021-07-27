package blanco.veevalidate;

import blanco.valueobjectts.BlancoValueObjectTsXmlParser;
import blanco.valueobjectts.valueobject.BlancoValueObjectTsClassStructure;
import blanco.veevalidate.task.valueobject.BlancoVeeValidateProcessInput;
import blanco.veevalidate.valueobject.BlancoVeeValidateClassStructure;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Obtains the list of Object created in BlancoValueObject from XML and stores it.
 *
 * Created by tueda on 15/07/05.
 */
public class BlancoVeeValidateUtil {

    static public boolean isVerbose = false;

    public static Map<String, BlancoValueObjectTsClassStructure> objects = new HashMap<>();
    /**
     * Contains the class name of the multilingual message.
     * K: lang
     * V: ValidationMessage[lang]
     */
    public static List<String> messageLangs = new ArrayList<>();

    static public void processValueObjects(final BlancoVeeValidateProcessInput input) throws IOException {
        if (isVerbose) {
            System.out.println("BlancoVeeValidateUtil : processValueObjects start !");
        }

        /* tmpdir is unique. */
        String baseTmpdir = input.getTmpdir();
        /* searchTmpdir is comma separated. */
        String tmpTmpdirs = input.getSearchTmpdir();
        List<String> searchTmpdirList = null;
        if (tmpTmpdirs != null && !tmpTmpdirs.equals(baseTmpdir)) {
            String[] searchTmpdirs = tmpTmpdirs.split(",");
            searchTmpdirList = new ArrayList<>(Arrays.asList(searchTmpdirs));
        }
        if (searchTmpdirList == null) {
            searchTmpdirList = new ArrayList<>();
        }
        searchTmpdirList.add(baseTmpdir);

        for (String tmpdir : searchTmpdirList) {
            searchTmpdir(tmpdir.trim());
        }
    }

    static private void searchTmpdir(String tmpdir) {

        // Reads information from XML-ized intermediate files.
        final File[] fileMeta3 = new File(tmpdir
                + BlancoVeeValidateConstants.OBJECT_SUBDIRECTORY)
                .listFiles();

        if (fileMeta3 == null) {
            System.out.println("!!! NO FILES in " + tmpdir
                    + BlancoVeeValidateConstants.OBJECT_SUBDIRECTORY);
            return;
        }

        for (int index = 0; index < fileMeta3.length; index++) {
            if (fileMeta3[index].getName().endsWith(".xml") == false) {
                continue;
            }

            BlancoValueObjectTsXmlParser parser = new BlancoValueObjectTsXmlParser();
//            parser.setVerbose(this.isVerbose());
            /*
             * First, it searches all the sheets and make a list of class and package names.
             * This is because in the php-style definitions, the package name is not specified when specifying class.
             */
            final BlancoValueObjectTsClassStructure[] structures = parser.parse(fileMeta3[index]);

            if (structures != null ) {
                for (int index2 = 0; index2 < structures.length; index2++) {
                    BlancoValueObjectTsClassStructure structure = structures[index2];
                    if (structure != null) {
                        if (isVerbose) {
                            System.out.println("processValueObjects: " + structure.getName());
                        }
                        objects.put(structure.getName(), structure);
                    } else {
                        System.out.println("processValueObjects: a structure is NULL!!!");
                    }
                }
            } else {
                System.out.println("processValueObjects: structures are NULL!!!");
            }
        }
    }

    /**
     * Make canonical classname into Simple.
     *
     * @param argClassNameCanon
     * @return simpleName
     */
    static public String getSimpleClassName(final String argClassNameCanon) {
        if (argClassNameCanon == null) {
            return "";
        }

        String simpleName = "";
        final int findLastDot = argClassNameCanon.lastIndexOf('.');
        if (findLastDot == -1) {
            simpleName = argClassNameCanon;
        } else if (findLastDot != argClassNameCanon.length() - 1) {
            simpleName = argClassNameCanon.substring(findLastDot + 1);
        }
        return simpleName;
    }

    /**
     * Make canonical classname into packageName
     *
     * @param argClassNameCanon
     * @return
     */
    static public String getPackageName(final String argClassNameCanon) {
        if (argClassNameCanon == null) {
            return "";
        }

        String simpleName = "";
        final int findLastDot = argClassNameCanon.lastIndexOf('.');
        if (findLastDot > 0) {
            simpleName = argClassNameCanon.substring(0, findLastDot);
        }
        return simpleName;
    }

    /**
     * Generates an import statement.
     * @param argPackageName
     * @param argClassName
     * @param argImportHeaderList
     * @param argBasedir
     * @param argPropertyPackage
     * @param skipClassFrom
     */
    static public void makeImportHeaderList(
            final String argPackageName,
            final String argClassName,
            final Map<String, List<String>> argImportHeaderList,
            final String argBasedir,
            final String argPropertyPackage,
            boolean skipClassFrom) {
        if (argClassName == null || argClassName.length() == 0) {
            System.out.println("BlancoVeeValidateUtil#makeImportHeaderList className is not specified. SKIP.");
            return;
        }
//        if (isVerbose) {
//            System.out.println("BlancoVeeValidateUtil#makeImportHeaderList: START : " + argPackageName + ", " + argClassName + ", " + argBasedir);
//        }
        String importFrom = "./" + argClassName;
        if (argPackageName != null &&
                argPackageName.length() != 0 &&
                argPackageName.equals(argPropertyPackage) != true) {
            String classNameCanon = argPackageName.replace('.', '/');
            if (!skipClassFrom) {
                classNameCanon += "/" + argClassName;
            }

            String basedir = "";
            if (argBasedir != null) {
                basedir = argBasedir;
            }
            importFrom = basedir + "/" + classNameCanon;
        }

        List<String> importClassList = argImportHeaderList.get(importFrom);
        if (importClassList == null) {
            importClassList = new ArrayList<>();
            argImportHeaderList.put(importFrom, importClassList);
        }
        boolean isMatch = false;
        for (String myClass : importClassList) {
            if (argClassName.equals(myClass)) {
                isMatch = true;
                break;
            }
        }
        if (!isMatch) {
            importClassList.add(argClassName);
            if (isVerbose) {
                System.out.println("BlancoVeeValidateUtil#makeImportHeaderList: new import { " + argClassName + " } from \"" + importFrom + "\"");
            }
        }
    }

    /**
     * Adds to importHeaderList with only checking for duplicates.
     *
     * @param argClassName
     * @param importFrom
     * @param argImportHeaderList
     */
    static public void addImportHeaderList(
            String argClassName,
            String importFrom,
            final Map<String, List<String>> argImportHeaderList
    ) {
        List<String> importClassList = argImportHeaderList.get(importFrom);
        if (importClassList == null) {
            importClassList = new ArrayList<>();
            argImportHeaderList.put(importFrom, importClassList);
        }
        boolean isMatch = false;
        for (String myClass : importClassList) {
            if (argClassName.equals(myClass)) {
                isMatch = true;
                break;
            }
        }
        if (!isMatch) {
            importClassList.add(argClassName);
            if (isVerbose) {
                System.out.println("BlancoVeeValidateUtil#addImportHeaderList: new import { " + argClassName + " } from \"" + importFrom + "\"");
            }
        }
    }

    static public String parsePhpTypes(
            final String phpType,
            final BlancoVeeValidateClassStructure argObjClassStructure,
            final Map<String, List<String>> argImportHeaderList,
            final boolean isGeneric) {
        String targetType = phpType;
        if ("boolean".equalsIgnoreCase(phpType)) {
            targetType = "boolean";
        } else if ("integer".equalsIgnoreCase(phpType)) {
            targetType = "number";
        } else if ("double".equalsIgnoreCase(phpType)) {
            targetType = "number";
        } else if ("float".equalsIgnoreCase(phpType)) {
            targetType = "number";
        } else if ("string".equalsIgnoreCase(phpType)) {
            targetType = "string";
        } else if ("array".equalsIgnoreCase(phpType)) {
            if (isGeneric) {
                throw new IllegalArgumentException("Cannot use array for genric type.");
            }
            targetType = "Array";
        } else if ("object".equalsIgnoreCase(phpType)) {
            targetType = "any";
        } else {
            /* Searches for a package with this name. */
            BlancoValueObjectTsClassStructure voStructure = BlancoVeeValidateUtil.objects.get(phpType);

            if (voStructure != null) {
                String tmpPhpType = phpType;
                String packageName = voStructure.getPackage();
                if (packageName == null || packageName.length() == 0) {
                    // Tries to isolate the package name.
                    String simpleName = BlancoVeeValidateUtil.getSimpleClassName(tmpPhpType);
                    if (simpleName != null && !simpleName.equals(phpType)) {
                        packageName = BlancoVeeValidateUtil.getPackageName(tmpPhpType);
                        tmpPhpType = simpleName;
                    }
                }
                if (packageName != null && packageName.length() > 0) {
                    targetType = packageName + "." + tmpPhpType;
                }

                /* Others are written as is. */
//                System.out.println("/* tueda */ Unknown php type: " + targetType);

                /*
                 * Creates import information for TypeScript.
                 * Note that the package may be the same as the component, but the basedir may be different.
                 */
                if (argObjClassStructure.getCreateImportList()) {
                    BlancoVeeValidateUtil.makeImportHeaderList(packageName, phpType, argImportHeaderList, voStructure.getBasedir(), "", false);
                }
            }
        }
        return targetType;
    }
}
