package blanco.veevalidate;

import blanco.valueobjectts.BlancoValueObjectTsXmlParser;
import blanco.valueobjectts.valueobject.BlancoValueObjectTsClassStructure;
import blanco.veevalidate.task.valueobject.BlancoVeeValidateProcessInput;
import blanco.veevalidate.valueobject.BlancoVeeValidateClassStructure;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * BlancoValueObject で作成されているObjectの一覧を XML から取得し，保持しておきます
 *
 * Created by tueda on 15/07/05.
 */
public class BlancoVeeValidateUtil {

    static public boolean isVerbose = false;

    public static Map<String, BlancoValueObjectTsClassStructure> objects = new HashMap<>();
    /**
     * 多言語メッセージのクラス名を保持します。
     * K: lang
     * V: ValidationMessage[lang]
     */
    public static List<String> messageLangs = new ArrayList<>();

    static public void processValueObjects(final BlancoVeeValidateProcessInput input) throws IOException {
        if (isVerbose) {
            System.out.println("BlancoVeeValidateUtil : processValueObjects start !");
        }

        /* tmpdir はユニーク */
        String baseTmpdir = input.getTmpdir();
        /* searchTmpdir はカンマ区切り */
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

        // XML化された中間ファイルから情報を読み込む
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
             * まず始めにすべてのシートを検索して，クラス名とpackage名のリストを作ります．
             * php形式の定義書では，クラスを指定する際にpackage名が指定されていないからです．
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
     * インポート文を生成する
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
     * importHeaderList に重複のチェックだけをして追加します。
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
            /* この名前の package を探す */
            BlancoValueObjectTsClassStructure voStructure = BlancoVeeValidateUtil.objects.get(phpType);

            if (voStructure != null) {
                String tmpPhpType = phpType;
                String packageName = voStructure.getPackage();
                if (packageName == null || packageName.length() == 0) {
                    // package 名の分離を試みる
                    String simpleName = BlancoVeeValidateUtil.getSimpleClassName(tmpPhpType);
                    if (simpleName != null && !simpleName.equals(phpType)) {
                        packageName = BlancoVeeValidateUtil.getPackageName(tmpPhpType);
                        tmpPhpType = simpleName;
                    }
                }
                if (packageName != null && packageName.length() > 0) {
                    targetType = packageName + "." + tmpPhpType;
                }

                /* その他はそのまま記述する */
//                System.out.println("/* tueda */ Unknown php type: " + targetType);

                /*
                 * TypeScript 用 import 情報の作成
                 * コンポーネントとはパッケージが同じでもbasedirが違う可能性がある事に注意。
                 */
                if (argObjClassStructure.getCreateImportList()) {
                    BlancoVeeValidateUtil.makeImportHeaderList(packageName, phpType, argImportHeaderList, voStructure.getBasedir(), "", false);
                }
            }
        }
        return targetType;
    }
}
