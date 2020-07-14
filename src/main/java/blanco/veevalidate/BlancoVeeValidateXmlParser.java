/*
 * blanco Framework
 * Copyright (C) 2004-2008 IGA Tosiki
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package blanco.veevalidate;

import blanco.cg.BlancoCgSupportedLang;
import blanco.commons.util.BlancoNameUtil;
import blanco.commons.util.BlancoStringUtil;
import blanco.valueobjectts.valueobject.BlancoValueObjectTsClassStructure;
import blanco.veevalidate.message.BlancoVeeValidateMessage;
import blanco.veevalidate.resourcebundle.BlancoVeeValidateResourceBundle;
import blanco.veevalidate.valueobject.BlancoVeeValidateClassStructure;
import blanco.veevalidate.valueobject.BlancoVeeValidateFieldStructure;
import blanco.veevalidate.valueobject.BlancoVeeValidateMessageStructure;
import blanco.xml.bind.BlancoXmlBindingUtil;
import blanco.xml.bind.BlancoXmlUnmarshaller;
import blanco.xml.bind.valueobject.BlancoXmlAttribute;
import blanco.xml.bind.valueobject.BlancoXmlDocument;
import blanco.xml.bind.valueobject.BlancoXmlElement;

import java.io.File;
import java.util.*;

/**
 * blancoValueObjectの 中間XMLファイル形式をパース(読み書き)するクラス。
 *
 * @author IGA Tosiki
 */
public class BlancoVeeValidateXmlParser {
    /**
     * メッセージ。
     */
    private final BlancoVeeValidateMessage fMsg = new BlancoVeeValidateMessage();

    private boolean fVerbose = false;

    public void setVerbose(boolean argVerbose) {
        this.fVerbose = argVerbose;
    }

    public boolean isVerbose() {
        return fVerbose;
    }

    /**
     * blancoValueObjectのリソースバンドルオブジェクト。
     */
    private final static BlancoVeeValidateResourceBundle fBundle = new BlancoVeeValidateResourceBundle();

    public static Map<String, Integer> mapCommons = new HashMap<String, Integer>() {
        {
            put(fBundle.getMeta2xmlElementCommon(), BlancoCgSupportedLang.PHP);
        } // PHP タイプシートにのみ対応

        {
            put(fBundle.getMeta2xmlElementCommonCs(), BlancoCgSupportedLang.CS);
        }

        {
            put(fBundle.getMeta2xmlElementCommonJs(), BlancoCgSupportedLang.JS);
        }

        {
            put(fBundle.getMeta2xmlElementCommonVb(), BlancoCgSupportedLang.VB);
        }

        {
            put(fBundle.getMeta2xmlElementCommonPhp(), BlancoCgSupportedLang.PHP);
        }

        {
            put(fBundle.getMeta2xmlElementCommonRuby(), BlancoCgSupportedLang.RUBY);
        }

        {
            put(fBundle.getMeta2xmlElementCommonPython(), BlancoCgSupportedLang.PYTHON);
        }

        {
            put(fBundle.getMeta2xmlElementCommonKt(), BlancoCgSupportedLang.KOTLIN);
        }

        {
            put(fBundle.getMeta2xmlElementCommonTs(), BlancoCgSupportedLang.TS);
        }
    };

    /**
     * 中間XMLファイルのXMLドキュメントをパースして、バリューオブジェクト情報の配列を取得します。
     *
     * @param argMetaXmlSourceFile 中間XMLファイル。
     * @return パースの結果得られたバリューオブジェクト情報の配列。
     */
    public BlancoVeeValidateClassStructure[] parse(
            final File argMetaXmlSourceFile) {
        final BlancoXmlDocument documentMeta = new BlancoXmlUnmarshaller()
                .unmarshal(argMetaXmlSourceFile);
        if (documentMeta == null) {
            return null;
        }

        return parse(documentMeta);

    }

    /**
     * 中間XMLファイル形式のXMLドキュメントをパースして、バリューオブジェクト情報の配列を取得します。
     *
     * @param argXmlDocument 中間XMLファイルのXMLドキュメント。
     * @return パースの結果得られたバリューオブジェクト情報の配列。
     */
    public BlancoVeeValidateClassStructure[] parse(
            final BlancoXmlDocument argXmlDocument) {
        final List<BlancoVeeValidateClassStructure> listStructure = new ArrayList<BlancoVeeValidateClassStructure>();

        // ルートエレメントを取得します。
        final BlancoXmlElement elementRoot = BlancoXmlBindingUtil
                .getDocumentElement(argXmlDocument);
        if (elementRoot == null) {
            // ルートエレメントが無い場合には処理中断します。
            return null;
        }

        // sheet(Excelシート)のリストを取得します。
        final List<BlancoXmlElement> listSheet = BlancoXmlBindingUtil
                .getElementsByTagName(elementRoot, "sheet");

        final int sizeListSheet = listSheet.size();
        for (int index = 0; index < sizeListSheet; index++) {
            final BlancoXmlElement elementSheet = listSheet.get(index);

            /*
             * Java以外の言語用に記述されたシートにも対応．
             */
            List<BlancoXmlElement> listCommon = null;
            int sheetLang = BlancoCgSupportedLang.JAVA;
            for (String common : mapCommons.keySet()) {
                listCommon = BlancoXmlBindingUtil
                        .getElementsByTagName(elementSheet,
                                common);
                if (listCommon.size() != 0) {
                    BlancoXmlAttribute attr = new BlancoXmlAttribute();
                    attr.setType("CDATA");
                    attr.setQName("style");
                    attr.setLocalName("style");

                    sheetLang = mapCommons.get(common);
                    attr.setValue(new BlancoCgSupportedLang().convertToString(sheetLang));

                    elementSheet.getAtts().add(attr);

                    /* tueda DEBUG */
                    if (this.isVerbose()) {
                        System.out.println("/* tueda */ style = " + BlancoXmlBindingUtil.getAttribute(elementSheet, "style"));
                    }

                    break;
                }
            }

            if (listCommon == null || listCommon.size() == 0) {
                // commonが無い場合にはスキップします。
                continue;
            }

            // 最初のアイテムのみ処理しています。
            final BlancoXmlElement elementCommon = listCommon.get(0);
            final String name = BlancoXmlBindingUtil.getTextContent(
                    elementCommon, "name");
            if (BlancoStringUtil.null2Blank(name).trim().length() == 0) {
                continue;
            }

            if (sheetLang != BlancoCgSupportedLang.PHP) {
                System.out.println("### ERROR Just PHP SheetLang is permitted.");
                throw new IllegalArgumentException("### ERROR: Invalid SheetLang : " + sheetLang);
            }

            BlancoVeeValidateClassStructure objClassStructure = parseElementSheetPhp(elementSheet);
            ;

            if (objClassStructure != null) {
                // 得られた情報を記憶します。
                listStructure.add(objClassStructure);
                /* メッセージの lang 情報を保存します。 */
                for (BlancoVeeValidateMessageStructure message : objClassStructure.getMessage()) {
                    String lang = message.getLang();
                    System.out.println("!!! message lang = " + lang);
                    if (lang != null && lang.length() > 0) {
                        BlancoVeeValidateUtil.messageLangs.add(lang.toLowerCase());
                    }
                }
            }
        }

        final BlancoVeeValidateClassStructure[] result = new BlancoVeeValidateClassStructure[listStructure
                .size()];
        listStructure.toArray(result);
        return result;
    }

    /**
     * 中間XMLファイル形式の「sheet」XMLエレメント(PHP書式)をパースして、バリューオブジェクト情報を取得します。
     *
     * @param argElementSheet 中間XMLファイルの「sheet」XMLエレメント。
     * @return パースの結果得られたバリューオブジェクト情報。「name」が見つからなかった場合には nullを戻します。
     */
    public BlancoVeeValidateClassStructure parseElementSheetPhp(
            final BlancoXmlElement argElementSheet
    ) {

        final BlancoVeeValidateClassStructure objClassStructure = new BlancoVeeValidateClassStructure();
        /*
         * パラメータの型にVOが使われている際に生成するheaderListを。objClassStructure#importList に格納します。
         */
        final Map<String, List<String>> importHeaderList = new HashMap<>();

        // 共通情報を取得します。
        final BlancoXmlElement elementCommon = BlancoXmlBindingUtil
                .getElement(argElementSheet, fBundle
                        .getMeta2xmlElementCommon());
        if (elementCommon == null) {
            // commonが無い場合には、このシートの処理をスキップします。
            return null;
        }

        final String name = BlancoXmlBindingUtil.getTextContent(
                elementCommon, "name");
        if (BlancoStringUtil.null2Blank(name).trim().length() == 0) {
            // nameが空の場合には処理をスキップします。
            return null;
        }

        if (this.isVerbose()) {
            System.out.println("BlancoVeeValidate#parseElementPhp name = " + name);
        }

        // validate定義・共通
        this.parseVeeValidateCommon(elementCommon, importHeaderList, objClassStructure);

        // validate定義・継承
        final List<BlancoXmlElement> extendsList = BlancoXmlBindingUtil
                .getElementsByTagName(argElementSheet, fBundle.getMeta2xmlElementExtends());
        if (extendsList != null && extendsList.size() != 0) {
            final BlancoXmlElement elementExtendsRoot = extendsList.get(0);
            parseVeeValidateExtends(elementExtendsRoot, importHeaderList, objClassStructure);
        }

        // Validate定義・メッセージ
        final List<BlancoXmlElement> messageList = BlancoXmlBindingUtil
                .getElementsByTagName(argElementSheet, fBundle.getMeta2xmlElementMessage());
        if (messageList != null && messageList.size() != 0) {
            final BlancoXmlElement elementListRoot = messageList.get(0);
            this.parseVeeValidateMessage(elementListRoot, objClassStructure);
        }


        // validate定義・パラメータ
        final List<BlancoXmlElement> listList = BlancoXmlBindingUtil
                .getElementsByTagName(argElementSheet, fBundle.getMeta2xmlElementList());
        if (listList != null && listList.size() != 0) {
            final BlancoXmlElement elementListRoot = listList.get(0);
            this.parseVeeValidateProperties(elementListRoot, importHeaderList, objClassStructure);
        }

        // ヘッダ情報を取得します。
        List<BlancoXmlElement> headerElementList = BlancoXmlBindingUtil
                .getElementsByTagName(argElementSheet,
                        fBundle.getMeta2xmlElementHeader());
        List<String> headerList = this.parseHeaderList(headerElementList, importHeaderList, null);
        if (headerList != null && headerList.size() > 0) {
            objClassStructure.getHeaderList().addAll(headerList);
        }

        return objClassStructure;
    }

    public static Map<String, String> createClassListFromSheets(final File[] argFileMeta) {
        Map<String, String> classList = new HashMap<String, String>();

        for (int index = 0; index < argFileMeta.length; index++) {
            File metaXmlSourceFile = argFileMeta[index];

            if (metaXmlSourceFile.getName().endsWith(".xml") == false) {
                continue;
            }

            final BlancoXmlDocument documentMeta = new BlancoXmlUnmarshaller()
                    .unmarshal(metaXmlSourceFile);
            if (documentMeta == null) {
                continue;
            }

            // ルートエレメントを取得します。
            final BlancoXmlElement elementRoot = BlancoXmlBindingUtil
                    .getDocumentElement(documentMeta);
            if (elementRoot == null) {
                // ルートエレメントが無い場合には処理中断します。
                continue;
            }

            // sheet(Excelシート)のリストを取得します。
            final List<BlancoXmlElement> listSheet = BlancoXmlBindingUtil
                    .getElementsByTagName(elementRoot, "sheet");


            for (BlancoXmlElement elementSheet : listSheet) {
                /*
                 * Java以外の言語用に記述されたシートにも対応．
                 */
                List<BlancoXmlElement> listCommon = null;
                for (String common : mapCommons.keySet()) {
                    listCommon = BlancoXmlBindingUtil
                            .getElementsByTagName(elementSheet,
                                    common);
                    if (listCommon.size() != 0) {
                        BlancoXmlElement elementCommon = listCommon.get(0);
                        classList.put(
                                BlancoXmlBindingUtil.getTextContent(elementCommon, "name"),
                                BlancoXmlBindingUtil.getTextContent(elementCommon, "package")
                        );

//                        System.out.println("/* tueda */ createClassList = " +
//                                BlancoXmlBindingUtil.getTextContent(elementCommon, "name") + " : " +
//                                BlancoXmlBindingUtil.getTextContent(elementCommon, "package"));
                        break;
                    }
                }
            }
        }

        return classList;
    }

    /**
     * 中間XMLファイルをパースして、「Validate定義・共通」を取得します。
     *
     * @param argElementCommon
     * @param argObjClassStructure
     */
    private void parseVeeValidateCommon(
            final BlancoXmlElement argElementCommon,
            final Map<String, List<String>> argImportHeaderList,
            final BlancoVeeValidateClassStructure argObjClassStructure
    ) {
        argObjClassStructure.setValidator(BlancoXmlBindingUtil.getTextContent(
                argElementCommon, "validator"));
        argObjClassStructure.setName(BlancoXmlBindingUtil.getTextContent(
                argElementCommon, "name"));
        argObjClassStructure.setPackage(BlancoXmlBindingUtil.getTextContent(
                argElementCommon, "package"));
        argObjClassStructure.setClassAlias(BlancoXmlBindingUtil.getTextContent(
                argElementCommon, "classAlias"));
        argObjClassStructure.setBasedir(BlancoXmlBindingUtil.getTextContent(
                argElementCommon, "basedir"));
        argObjClassStructure.setImpledir(BlancoXmlBindingUtil.getTextContent(
                argElementCommon, "impledir"));

        argObjClassStructure.setDescription(BlancoXmlBindingUtil.getTextContent(
                argElementCommon, "description"));
        if (BlancoStringUtil.null2Blank(argObjClassStructure.getDescription())
                .length() > 0) {
            final String[] lines = BlancoNameUtil.splitString(argObjClassStructure
                    .getDescription(), '\n');
            for (int index = 0; index < lines.length; index++) {
                if (index == 0) {
                    argObjClassStructure.setDescription(lines[index]);
                } else {
                    // 複数行の description については、これを分割して格納します。
                    // ２行目からは、適切に文字参照エンコーディングが実施されているものと仮定します。
                    argObjClassStructure.getDescriptionList().add(lines[index]);
                }
            }
        }
        argObjClassStructure.setFileDescription(BlancoXmlBindingUtil.getTextContent(
                argElementCommon, "fileDescription"));

        /*
         * validatorの種類： builtin, custom, config, message
         */
        argObjClassStructure.setValidatorKind(BlancoXmlBindingUtil.getTextContent(
                argElementCommon, "validatorKind"));

        argObjClassStructure.setAlterMessage("true".equals(BlancoXmlBindingUtil
                .getTextContent(argElementCommon, "alterMessage")));

        argObjClassStructure.setAdjustFieldName("true".equals(BlancoXmlBindingUtil
                .getTextContent(argElementCommon, "adjustFieldName")));
        argObjClassStructure.setCreateImportList("true"
                .equals(BlancoXmlBindingUtil.getTextContent(argElementCommon,
                        "createImportList")));

        argObjClassStructure
                .setFieldList(new ArrayList<>());


        /*
         * RuleSchema のための import文を生成しておく
         * message では import の自動生成は必要ない。
         * config では使っているすべてのvalidatorを、別途importする必要がある
         */
        String kind = argObjClassStructure.getValidatorKind();
        if (!"message".equalsIgnoreCase(kind) && argObjClassStructure.getCreateImportList()) {
            String validator = argObjClassStructure.getValidator();
            String packageName = "dist.rules";
            String impleDir = "vee-validate";
            boolean isCustom = false;
            if ("custom".equalsIgnoreCase(kind)) {
                validator += "Validator";
                packageName = BlancoStringUtil.null2Blank(argObjClassStructure.getPackage());
                impleDir = BlancoStringUtil.null2Blank(argObjClassStructure.getImpledir());
                isCustom = true;
            }
            BlancoVeeValidateUtil.makeImportHeaderList(packageName, validator, argImportHeaderList, impleDir, "", !isCustom);
        }
    }

    /**
     * 中間XMLファイル形式のXMLドキュメントをパースして、「電文定義・継承」を取得します。
     * @param argElementExtendsRoot
     * @param argImportHeaderList
     * @param argClassStructure
     */
    private void parseVeeValidateExtends(
            final BlancoXmlElement argElementExtendsRoot,
            final Map<String, List<String>> argImportHeaderList,
            final BlancoVeeValidateClassStructure argClassStructure) {

        String className = BlancoXmlBindingUtil.getTextContent(argElementExtendsRoot, "name");
        if (className != null) {
            String classNameCanon = className;
            String packageName = BlancoXmlBindingUtil.getTextContent(argElementExtendsRoot, "package");
            if (packageName == null) {
                /*
                 * このクラスのパッケージ名を探す
                 */
                BlancoValueObjectTsClassStructure voStructure = BlancoVeeValidateUtil.objects.get(className);
                if (voStructure != null) {
                    packageName = voStructure.getPackage();
                }
            }
            if (packageName != null) {
                classNameCanon = packageName + "." + className;
            }
            if (isVerbose()) {
                System.out.println("Extends : " + classNameCanon);
            }
            argClassStructure.setExtends(classNameCanon);

            /*
             * TypeScript 用 import 情報の作成
             */
            if (argClassStructure.getCreateImportList()) {
                BlancoVeeValidateUtil.makeImportHeaderList(packageName, className, argImportHeaderList, argClassStructure.getBasedir(), argClassStructure.getPackage(), false);
            }
        } else {
            System.out.println("/* Extends Skip */ className is not specified!!!");
        }
    }

    private void parseVeeValidateMessage(
            final BlancoXmlElement argElementMessage,
            final BlancoVeeValidateClassStructure argObjClassStructure
    ) {
        final List<BlancoXmlElement> listChildNodes = BlancoXmlBindingUtil
                .getElementsByTagName(argElementMessage, "message");
        for (int index = 0; index < listChildNodes.size(); index++) {
            final BlancoXmlElement elementList = listChildNodes.get(index);
            final BlancoVeeValidateMessageStructure messageStructure = new BlancoVeeValidateMessageStructure();

            messageStructure.setNo(BlancoXmlBindingUtil.getTextContent(
                    elementList, "no"));
            messageStructure.setMessage(BlancoXmlBindingUtil.getTextContent(
                    elementList, "template"));
            if (messageStructure.getMessage() == null
                    || messageStructure.getMessage().trim().length() == 0) {
                continue;
            }
            messageStructure.setLang(BlancoXmlBindingUtil.getTextContent(
                    elementList, "lang"));
            if (messageStructure.getLang() == null
                    || messageStructure.getLang().trim().length() == 0) {
                continue;
            }
            argObjClassStructure.getMessage().add(messageStructure);
        }
    }

    private void parseVeeValidateProperties(
            final BlancoXmlElement argElementProperties,
            final Map<String, List<String>> argImportHeaderList,
            final BlancoVeeValidateClassStructure argObjClassStructure
    ) {
        final List<BlancoXmlElement> listChildNodes = BlancoXmlBindingUtil
                .getElementsByTagName(argElementProperties, "field");
        for (int index = 0; index < listChildNodes.size(); index++) {
            final BlancoXmlElement elementList = listChildNodes.get(index);
            final BlancoVeeValidateFieldStructure fieldStructure = new BlancoVeeValidateFieldStructure();

            fieldStructure.setNo(BlancoXmlBindingUtil.getTextContent(
                    elementList, "no"));
            fieldStructure.setName(BlancoXmlBindingUtil.getTextContent(
                    elementList, "name"));
            if (fieldStructure.getName() == null
                    || fieldStructure.getName().trim().length() == 0) {
                continue;
            }

            /*
             * 型の取得．ここで TypeScript 風の型名に変えておく
             */
            String phpType = BlancoXmlBindingUtil.getTextContent(elementList, "type");
            String targetType = BlancoVeeValidateUtil.parsePhpTypes(phpType, argObjClassStructure, argImportHeaderList, false);
            fieldStructure.setType(targetType);

            /* Generic に対応 */
            String phpGeneric = BlancoXmlBindingUtil.getTextContent(elementList, "generic");
            if (BlancoStringUtil.null2Blank(phpGeneric).length() != 0) {
                String targetGeneric = BlancoVeeValidateUtil.parsePhpTypes(phpGeneric, argObjClassStructure, argImportHeaderList, true);
                fieldStructure.setGeneric(targetGeneric);
            }

            // Nullable に対応
            fieldStructure.setNullable("true".equals(BlancoXmlBindingUtil
                    .getTextContent(elementList, "nullable")));

            // 説明
            fieldStructure.setDescription(BlancoXmlBindingUtil
                    .getTextContent(elementList, "description"));
            final String[] lines = BlancoNameUtil.splitString(
                    fieldStructure.getDescription(), '\n');
            for (int indexLine = 0; indexLine < lines.length; indexLine++) {
                if (indexLine == 0) {
                    fieldStructure.setDescription(lines[indexLine]);
                } else {
                    // 複数行の description については、これを分割して格納します。
                    // ２行目からは、適切に文字参照エンコーディングが実施されているものと仮定します。
                    fieldStructure.getDescriptionList().add(
                            lines[indexLine]);
                }
            }

            // デフォルト
            fieldStructure.setDefault(BlancoXmlBindingUtil.getTextContent(
                    elementList, "default"));

            // 必須
            fieldStructure.setNullable("true".equals(BlancoXmlBindingUtil
                    .getTextContent(elementList, "required")));

            argObjClassStructure.getFieldList().add(fieldStructure);
        }
    }

    /**
     *
     *
     * @param argHeaderElementList
     * @param argImportHeaderList
     * @return
     */
    private List<String> parseHeaderList(
            final List<BlancoXmlElement> argHeaderElementList,
            final Map<String, List<String>> argImportHeaderList,
            final Map<String, List<String>> argDefaultExportedHeaderList
    ) {
        if (this.isVerbose()) {
            System.out.println("BlancoVeeValidate#parseHeaderList: Start parseHeaderList.");
        }

        List<String> headerList = new ArrayList<>();

        /*
         * header の一覧作成
         * まず、定義書に書かれたものをそのまま出力します。
         */
        if (argHeaderElementList != null && argHeaderElementList.size() > 0) {
            final BlancoXmlElement elementHeaderRoot = argHeaderElementList.get(0);
            final List<BlancoXmlElement> listHeaderChildNodes = BlancoXmlBindingUtil
                    .getElementsByTagName(elementHeaderRoot, "header");
            for (int index = 0; index < listHeaderChildNodes.size(); index++) {
                final BlancoXmlElement elementList = listHeaderChildNodes
                        .get(index);

                final String headerName = BlancoXmlBindingUtil
                        .getTextContent(elementList, "name");
                if (this.isVerbose()) {
                    System.out.println("BlancoVeeValidate#parseHeaderList header = " + headerName);
                }
                if (headerName == null || headerName.trim().length() == 0) {
                    continue;
                }
                headerList.add(
                        BlancoXmlBindingUtil
                                .getTextContent(elementList, "name"));
            }
        }

        /*
         * 次に、自動生成されたものを出力します。
         * 現在の方式だと、以下の前提が必要。
         *  * 1ファイルに1クラスの定義
         *  * 定義シートでは Java/kotlin 式の package 表記でディレクトリを表現
         * TODO: 定義シート上にファイルの配置ディレクトリを定義できるようにすべし？
         */
        this.parseGeneratedHeaderList(headerList, argImportHeaderList, false);

        /*
         * 次に、expor default されたものを出力します。
         * 現在の方式だと、以下の前提が必要。
         *  * 1ファイルに1クラスの定義
         *  * 定義シートでは Java/kotlin 式の package 表記でディレクトリを表現
         * TODO: 定義シート上にファイルの配置ディレクトリを定義できるようにすべし？
         */
        if (argDefaultExportedHeaderList != null && argDefaultExportedHeaderList.size() > 0) {
            this.parseGeneratedHeaderList(headerList, argDefaultExportedHeaderList, true);
        }

        return headerList;
    }

    private void parseGeneratedHeaderList(
            List<String> argHeaderList,
            final Map<String, List<String>> argImportHeaderList,
            boolean isDefaultExported
    ) {
        if (argImportHeaderList != null && argImportHeaderList.size() > 0) {
            Set<String> fromList = argImportHeaderList.keySet();
            for (String strFrom : fromList) {
                StringBuffer sb = new StringBuffer();
                if (isDefaultExported) {
                    sb.append("import ");
                } else {
                    sb.append("import { ");
                }
                List<String> classNameList = argImportHeaderList.get(strFrom);
                int count = 0;
                for (String className : classNameList) {
                    if (count > 0) {
                        sb.append(", ");
                    }
                    sb.append(className);
                    count++;
                }
                if (count > 0) {
                    if (isDefaultExported) {
                        sb.append(" from \"" + strFrom + "\"");
                    } else {
                        sb.append(" } from \"" + strFrom + "\"");
                    }
                    if (this.isVerbose()) {
                        System.out.println("BlancoRestGeneratorTsXmlParser#parseGeneratedHeaderList import = " + sb.toString());
                    }
                    argHeaderList.add(sb.toString());
                }
            }
        }
    }
}
