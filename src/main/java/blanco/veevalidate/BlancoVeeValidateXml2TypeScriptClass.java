/*
 * blanco Framework
 * Copyright (C) 2004-2010 IGA Tosiki
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package blanco.veevalidate;

import blanco.cg.BlancoCgObjectFactory;
import blanco.cg.BlancoCgSupportedLang;
import blanco.cg.transformer.BlancoCgTransformerFactory;
import blanco.cg.valueobject.*;
import blanco.commons.util.BlancoJavaSourceUtil;
import blanco.commons.util.BlancoNameAdjuster;
import blanco.commons.util.BlancoStringUtil;
import blanco.veevalidate.message.BlancoVeeValidateMessage;
import blanco.veevalidate.resourcebundle.BlancoVeeValidateResourceBundle;
import blanco.veevalidate.valueobject.BlancoVeeValidateClassStructure;
import blanco.veevalidate.valueobject.BlancoVeeValidateFieldStructure;
import blanco.veevalidate.valueobject.BlancoVeeValidateMessageStructure;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * バリューオブジェクト用中間XMLファイルから TypeScript ソースコードを自動生成するクラス。
 *
 * BlancoValueObjectTsの主たるクラスのひとつです。
 *
 * @author IGA Tosiki
 * @author tueda
 */
public class BlancoVeeValidateXml2TypeScriptClass {
    /**
     * メッセージ。
     */
    private final BlancoVeeValidateMessage fMsg = new BlancoVeeValidateMessage();

    /**
     * blancoValueObjectのリソースバンドルオブジェクト。
     */
    private final BlancoVeeValidateResourceBundle fBundle = new BlancoVeeValidateResourceBundle();

    /**
     * 入力シートに期待するプログラミング言語
     */
    private int fSheetLang = BlancoCgSupportedLang.PHP;

    public void setSheetLang(final int argSheetLang) {
        fSheetLang = argSheetLang;
    }

    /**
     * ソースコード生成先ディレクトリのスタイル
     */
    private boolean fTargetStyleAdvanced = false;
    public void setTargetStyleAdvanced(boolean argTargetStyleAdvanced) {
        this.fTargetStyleAdvanced = argTargetStyleAdvanced;
    }
    public boolean isTargetStyleAdvanced() {
        return this.fTargetStyleAdvanced;
    }

    private boolean fVerbose = false;
    public void setVerbose(boolean argVerbose) {
        this.fVerbose = argVerbose;
    }
    public boolean isVerbose() {
        return this.fVerbose;
    }

    private int fTabs = 4;
    public int getTabs() {
        return fTabs;
    }
    public void setTabs(int fTabs) {
        this.fTabs = fTabs;
    }

    public String getConfigFile() {
        return fConfigFile;
    }

    public void setConfigFile(String configFile) {
        this.fConfigFile = configFile;
    }

    private String fConfigFile = "";

    /**
     * 内部的に利用するblancoCg用ファクトリ。
     */
    private BlancoCgObjectFactory fCgFactory = null;

    /**
     * 内部的に利用するblancoCg用ソースファイル情報。
     */
    private BlancoCgSourceFile fCgSourceFile = null;

    /**
     * 内部的に利用するblancoCg用クラス情報。
     */
    private BlancoCgClass fCgClass = null;

    /**
     * 内部的に利用するblancoCg用インタフェイス情報。
     */
    private BlancoCgInterface fCgInterface = null;

    /**
     * 自動生成するソースファイルの文字エンコーディング。
     */
    private String fEncoding = null;

    public void setEncoding(final String argEncoding) {
        fEncoding = argEncoding;
    }

    private boolean fIsXmlRootElement = false;

    public void setXmlRootElement(final boolean isXmlRootElement) {
        fIsXmlRootElement = isXmlRootElement;
    }

    public void process(
        final List<BlancoVeeValidateClassStructure> argClassStructures,
        final File argDirectoryTarget) throws IOException {

        /*
         * まずはじめに、ValidationRuleSchema を生成します。
         */
        generateValidationRuleSchema(argClassStructures, argDirectoryTarget);

        /*
         * 次に各国語のメッセージ定義ファイルを生成します
         */
        generateValidationMessages(argClassStructures, argDirectoryTarget);


        /*
         * 次に設定クラスファイルを生成します。
         */
        generateConfigClass(argClassStructures, argDirectoryTarget);

    }

    /**
     * ValidationRuleSchema を生成します。
     *
     * @param argClassStructures
     * @param argDirectoryTarget
     * @throws IOException
     */
    private void generateValidationRuleSchema(
            final List<BlancoVeeValidateClassStructure> argClassStructures,
            final File argDirectoryTarget) throws IOException {
        /*
         * 出力ディレクトリはant taskのtargetStyel引数で
         * 指定された書式で出力されます。
         * 従来と互換性を保つために、指定がない場合は blanco/main
         * となります。
         * by tueda, 2019/08/30
         */
        String strTarget = argDirectoryTarget
                .getAbsolutePath(); // advanced
        if (!this.isTargetStyleAdvanced()) {
            strTarget += "/main"; // legacy
        }
        final File fileBlancoMain = new File(strTarget);

        /* tueda DEBUG */
        if (this.isVerbose()) {
            System.out.println("/* tueda */ generateValidationRuleSchema argDirectoryTarget : " + argDirectoryTarget.getAbsolutePath());
        }

        String schemaSuffix = "RuleSchema";
        String customRuleSuffix = "Validator";
        for (BlancoVeeValidateClassStructure structure : argClassStructures) {
            String validatorKind = structure.getValidatorKind();
            if (("builtin".equalsIgnoreCase(validatorKind) &&
                    !structure.getAlterMessage()) ||
            "message".equalsIgnoreCase(validatorKind) ||
            "config".equalsIgnoreCase(validatorKind)) {
                continue;
            }

            String simpleClassName = BlancoNameAdjuster.toClassName(structure.getName()) + schemaSuffix;
            String packageName = BlancoStringUtil.null2Blank(structure.getPackage());
            // BlancoCgObjectFactoryクラスのインスタンスを取得します。
            fCgFactory = BlancoCgObjectFactory.getInstance();
            fCgSourceFile = fCgFactory.createSourceFile(packageName, "このソースコードはblanco Frameworkによって作成されました。");
            fCgSourceFile.setEncoding(fEncoding);
            fCgSourceFile.setTabs(this.getTabs());

            // クラスを作成します。
            fCgClass = fCgFactory.createClass(simpleClassName, fBundle.getXml2sourceFileValidateRuleSchema());
            fCgSourceFile.getClassList().add(fCgClass);

            fCgClass.getLangDoc().getDescriptionList().add(structure.getDescription());
            fCgClass.getLangDoc().getDescriptionList().addAll(structure.getDescriptionList());

            fCgClass.setAccess("public");
            // implements ValidationRuleSchema
            BlancoCgType impleType = new BlancoCgType();
            fCgClass.getImplementInterfaceList().add(impleType);
            impleType.setName("ValidationRuleSchema");

            // RuleSchema の中身を生成します。
            if ("custom".equalsIgnoreCase(validatorKind)) {
                fCgClass.getPlainTextList().add(this.generateCustomRuleSchema(structure));
            } else if ("builtin".equalsIgnoreCase(validatorKind)) {
                fCgClass.getPlainTextList().add(this.generateBuiltinRuleSchema(structure));
            } else {
                throw new IllegalArgumentException("Cannot generate RuleSchema for " + validatorKind);
            }

            /* TypeScript では import の代わりに header を設定します */
            for (String header : structure.getHeaderList()) {
                fCgSourceFile.getHeaderList().add(header);
            }

            // 収集された情報を元に実際のソースコードを自動生成。
            BlancoCgTransformerFactory.getTsSourceTransformer().transform(
                    fCgSourceFile, fileBlancoMain);
        }
    }

    /**
     * カスタムルールのschemaを生成します。
     * @param argStructure
     * @return
     */
    private String generateCustomRuleSchema(BlancoVeeValidateClassStructure argStructure) {
        StringBuffer sb = new StringBuffer();
        String customRuleSuffix = "Validator";

        // はじめにfieldのリストをつくっておきます。
        String fields = "";
        int i = 0;
        for (BlancoVeeValidateFieldStructure field : argStructure.getFieldList()) {
            if (i > 0) {
                fields += ", ";
            }
            fields += "\"" + field.getName() + "\"";
            i++;
        }

        sb.append("validate: ValidationRuleFunction = (value, params) => {" + this.getLineSeparator());
        sb.append(this.getTabSpace(2) +
                "return " + argStructure.getValidator() + customRuleSuffix +
                "(value, params);" +
                this.getLineSeparator()
                );
        sb.append(this.getTabSpace(1) + "};" + this.getLineSeparator());
        if (fields.length() > 0) {
            sb.append(this.getTabSpace(1) + "params: RuleParamSchema[] = [" + fields + "];" + this.getLineSeparator());
        }
        sb.append(this.getTabSpace(1) + "message: ValidationMessageTemplate = (field: string, params?: Record<string, any>) => {" + this.getLineSeparator());
        sb.append(this.getTabSpace(2) + "return " + BlancoVeeValidateConstants.CUSTOM_MESSAGE + "(field, params");
        if (fields.length() > 0) {
            sb.append(", [" + fields + "]");
        }
        sb.append(");" + this.getLineSeparator());
        sb.append(this.getTabSpace(1) + "};" + this.getLineSeparator());

        return sb.toString();
    }

    /**
     * 組み込みルールのschemaを生成します。
     * @param argStructure
     * @return
     */
    private String generateBuiltinRuleSchema(BlancoVeeValidateClassStructure argStructure) {
        StringBuffer sb = new StringBuffer();

        sb.append("validate: ValidationRuleFunction = " + argStructure.getValidator() + ".validate;" + this.getLineSeparator());
        sb.append(this.getTabSpace(1) + "params: RuleParamSchema[] = " + argStructure.getValidator() + ".params;" + this.getLineSeparator());
        sb.append(this.getTabSpace(1) + "message: ValidationMessageTemplate = (field: string, params?: Record<string, any>) => {" + this.getLineSeparator());
        sb.append(this.getTabSpace(2) + "return " + BlancoVeeValidateConstants.CUSTOM_MESSAGE + "(field, params, " +
                argStructure.getValidator() + ".params);" + this.getLineSeparator());
        sb.append(this.getTabSpace(1) + "};" + this.getLineSeparator());

        return sb.toString();
    }

    /**
     * カスタムメッセージを生成します。
     *
     * @param argClassStructures
     * @param argDirectoryTarget
     * @throws IOException
     */
    private void generateValidationMessages(
            final List<BlancoVeeValidateClassStructure> argClassStructures,
            final File argDirectoryTarget) throws IOException {
        /*
         * 出力ディレクトリはant taskのtargetStyel引数で
         * 指定された書式で出力されます。
         * 従来と互換性を保つために、指定がない場合は blanco/main
         * となります。
         * by tueda, 2019/08/30
         */
        String strTarget = argDirectoryTarget
                .getAbsolutePath(); // advanced
        if (!this.isTargetStyleAdvanced()) {
            strTarget += "/main"; // legacy
        }
        final File fileBlancoMain = new File(strTarget);

        /* tueda DEBUG */
        if (this.isVerbose()) {
            System.out.println("/* tueda */ generateValidationMessages argDirectoryTarget : " + argDirectoryTarget.getAbsolutePath());
        }

        /*
         * まずはじめに、メッセージクラス定義を検索します。
         */
        BlancoVeeValidateClassStructure messageStructure = searchValidateClassByKind(argClassStructures, "message");
        if (messageStructure == null) {
            throw new IllegalArgumentException(this.fBundle.getXml2sourceFileErr008());
        }

        for (String lang : BlancoVeeValidateUtil.messageLangs) {
            if (this.isVerbose()) {
                System.out.println("BlancoVeeVaidate: Lang = " + lang);
            }
            String simpleClassName = messageStructure.getName() + BlancoNameAdjuster.toClassName(lang);
            String packageName = BlancoStringUtil.null2Blank(messageStructure.getPackage());

            // BlancoCgObjectFactoryクラスのインスタンスを取得します。
            fCgFactory = BlancoCgObjectFactory.getInstance();
            fCgSourceFile = fCgFactory.createSourceFile(packageName, "このソースコードはblanco Frameworkによって作成されました。");
            fCgSourceFile.setEncoding(fEncoding);
            fCgSourceFile.setTabs(this.getTabs());

            // クラスを作成します。
            fCgClass = fCgFactory.createClass(simpleClassName, fBundle.getXml2sourceFileValidateMessage());
            fCgSourceFile.getClassList().add(fCgClass);
            fCgClass.setAccess("public");

            // 継承に対応します。
            if (BlancoStringUtil.null2Blank(messageStructure.getExtends())
                    .length() > 0) {
                fCgClass.getExtendClassList().add(
                        fCgFactory.createType(messageStructure.getExtends()));
            }

            // メッセージをfieldとして定義していきます。
            generateValidationMessageLang(lang, argClassStructures);

            /* TypeScript では import の代わりに header を設定します */
            for (String header : messageStructure.getHeaderList()) {
                fCgSourceFile.getHeaderList().add(header);
            }

            // 収集された情報を元に実際のソースコードを自動生成。
            BlancoCgTransformerFactory.getTsSourceTransformer().transform(
                    fCgSourceFile, fileBlancoMain);
        }
    }

    /**
     * メッセージを定義するクラス名を探します。
     *
     * @param argClassStructures
     * @return
     */
    private BlancoVeeValidateClassStructure searchValidateClassByKind(
            final List<BlancoVeeValidateClassStructure> argClassStructures,
            final String argKind
    ) {
        BlancoVeeValidateClassStructure found = null;
        if (argKind == null || argKind.length() == 0) {
            return found;
        }
        for (BlancoVeeValidateClassStructure structure : argClassStructures) {
            if (argKind.equalsIgnoreCase(structure.getValidatorKind())) {
                found = structure;
                break;
            }
        }
        return found;
    }

    /**
     * 各言語毎のカスタムメッセージを生成します。
     *
     * @param lang
     * @param argClassStructures
     */
    private void generateValidationMessageLang(
            final String lang,
            final List<BlancoVeeValidateClassStructure> argClassStructures
    ) {
        for (BlancoVeeValidateClassStructure structure : argClassStructures) {
            String ruleId = structure.getName();
            List<BlancoVeeValidateMessageStructure> messageList = structure.getMessage();
            for (BlancoVeeValidateMessageStructure message : messageList) {
                if (!lang.equalsIgnoreCase(message.getLang())) {
                    continue;
                }
                final BlancoCgField field = fCgFactory.createField(ruleId,
                        "string", null);
                field.setDefault(
                        // ダブルクオートを付与します。
                        "\"" + BlancoJavaSourceUtil.escapeStringAsJavaSource(message.getMessage()) + "\"");
                field.setAccess("public");
                field.setNotnull(true);

                field.getLangDoc().getDescriptionList().add(
                        structure.getValidator() + ": " + BlancoJavaSourceUtil.escapeStringAsJavaSource(message.getMessage())
                );
                for (BlancoVeeValidateFieldStructure param: structure.getFieldList()) {
                    /* パラメータの1行目のみを記載 */
                    String desc = param.getDescription();
                    if (desc != null && desc.length() > 0) {
                        field.getLangDoc().getDescriptionList().add(
                                "[" + param.getName() + "] " +
                                        desc
                        );
                    }
                }

                fCgClass.getFieldList().add(field);
            }
        }
    }

    /**
     * 与えられたクラス情報バリューオブジェクトから、ソースコードを自動生成します。
     *
     * @param argClassStructures
     *            クラス情報
     * @param argDirectoryTarget
     *            TypeScript ソースコードの出力先ディレクトリ
     * @throws IOException
     *             入出力例外が発生した場合。
     */
    public void generateConfigClass(
            final List<BlancoVeeValidateClassStructure> argClassStructures,
            final File argDirectoryTarget) throws IOException {
        /*
         * 出力ディレクトリはant taskのtargetStyel引数で
         * 指定された書式で出力されます。
         * 従来と互換性を保つために、指定がない場合は blanco/main
         * となります。
         * by tueda, 2019/08/30
         */
        String strTarget = argDirectoryTarget
                .getAbsolutePath(); // advanced
        if (!this.isTargetStyleAdvanced()) {
            strTarget += "/main"; // legacy
        }
        final File fileBlancoMain = new File(strTarget);

        /* tueda DEBUG */
        if (this.isVerbose()) {
            System.out.println("/* tueda */ generateClass argDirectoryTarget : " + argDirectoryTarget.getAbsolutePath());
        }

        /*
         * まずはじめに、Configクラス定義を検索します。
         */
        BlancoVeeValidateClassStructure configStructure = searchValidateClassByKind(argClassStructures, "config");
        if (configStructure == null) {
            throw new IllegalArgumentException(this.fBundle.getXml2sourceFileErr007());
        }

        // BlancoCgObjectFactoryクラスのインスタンスを取得します。
        fCgFactory = BlancoCgObjectFactory.getInstance();

        fCgSourceFile = fCgFactory.createSourceFile(configStructure
                .getPackage(), "このソースコードはblanco Frameworkによって作成されました。");
        fCgSourceFile.setEncoding(fEncoding);
        fCgSourceFile.setTabs(this.getTabs());

        // クラスを作成します。
        fCgClass = fCgFactory.createClass(configStructure.getName(), this.fBundle.getXml2sourceFileValidateInit());
        fCgSourceFile.getClassList().add(fCgClass);
        fCgClass.setAccess("public");

        // 継承
        if (BlancoStringUtil.null2Blank(configStructure.getExtends())
                .length() > 0) {
            fCgClass.getExtendClassList().add(
                    fCgFactory.createType(configStructure.getExtends()));
        }
        if (fIsXmlRootElement) {
            fCgClass.getAnnotationList().add("XmlRootElement");
            fCgSourceFile.getImportList().add(
                    "javax.xml.bind.annotation.XmlRootElement");
        }

        // クラスのJavaDocを設定します。
        fCgClass.setDescription(configStructure.getDescription());
        for (String line : configStructure.getDescriptionList()) {
            fCgClass.getLangDoc().getDescriptionList().add(line);
        }

        // setupメソッドを生成します。
        buildSetupMethod(argClassStructures, configStructure);

        /* TypeScript では import の代わりに header を設定します */
        for (String header : configStructure.getHeaderList()) {
            fCgSourceFile.getHeaderList().add(header);
        }

        // 収集された情報を元に実際のソースコードを自動生成。
        BlancoCgTransformerFactory.getTsSourceTransformer().transform(
                fCgSourceFile, fileBlancoMain);
    }

    private void buildSetupMethod(
            final List<BlancoVeeValidateClassStructure> argClassStructures,
            final BlancoVeeValidateClassStructure argConfigStructure
    ) {
        // おのおののフィールドに対するゲッターメソッドを生成します。
        final BlancoCgMethod method = fCgFactory.createMethod(BlancoVeeValidateConstants.INIT_METHOD,
                fBundle.getXml2sourceFileValidateInitMethod());
        fCgClass.getMethodList().add(method);
        method.setNotnull(true);
        method.setAccess("public");
        method.setStatic(true);
        method.setAccess("static");

        method.getLineList().add("configure(validateConfig);");
        method.getLineList().add("");
        method.getLineList().add("/* builtin rules */");

        List<String> builtinNormal = new ArrayList<>();
        List<String> builtinAlter = new ArrayList<>();

        // まずbuiltinルールを設定します。
        for (BlancoVeeValidateClassStructure structure : argClassStructures) {
            if ("builtin".equalsIgnoreCase(structure.getValidatorKind())) {
                boolean isAlter = false;
                String validator = structure.getValidator();
                if (structure.getAlterMessage()) {
                    validator = "new " + BlancoNameAdjuster.toClassName(structure.getValidator()) + "RuleSchema()";
                    isAlter = true;
                }
                method.getLineList().add("extend(\"" +
                        structure.getName() + "\", " +
                        validator + ");");
                if (isAlter) {
                    builtinAlter.add(this.makeImportForRuleSchema(structure));
                } else {
                    builtinNormal.add(structure.getValidator());
                }
            }
        }
        if (builtinNormal.size() > 0) {
            StringBuffer builtinImport = new StringBuffer();
            builtinImport.append("import { " + this.getLineSeparator());
            int i = 0;
            for (String validator : builtinNormal) {
                if (i > 0) {
                    builtinImport.append("," + this.getLineSeparator());
                }
                builtinImport.append(this.getTabSpace(1) + validator);
                i++;
            }
            builtinImport.append(this.getLineSeparator());
            builtinImport.append("} from \"vee-validate/dist/rules\"");
            argConfigStructure.getHeaderList().add(builtinImport.toString());
        }
        if (builtinAlter.size() > 0) {
            argConfigStructure.getHeaderList().addAll(builtinAlter);
        }

        method.getLineList().add("");
        method.getLineList().add("/* custom rules */");

        List<String> customImport = new ArrayList<>();
        // 次にカスタムルールを設定します。
        for (BlancoVeeValidateClassStructure structure : argClassStructures) {
            if ("custom".equalsIgnoreCase(structure.getValidatorKind())) {
                String validator = "new " + BlancoNameAdjuster.toClassName(structure.getValidator()) + "RuleSchema()";
                method.getLineList().add("extend(\"" +
                        structure.getName() + "\", " +
                        validator + ");");
                customImport.add(this.makeImportForRuleSchema(structure));
            }
        }
        if (customImport.size() > 0) {
            argConfigStructure.getHeaderList().addAll(customImport);
        }
    }

    private String getTabSpace(int indent) {
        StringBuffer spc = new StringBuffer();
        for (int j = 0; j < indent; j++) {
            for (int i = this.getTabs(); i > 0; i--) {
                spc.append(" ");
            }
        }
        return spc.toString();
    }

    private String getLineSeparator() {
        return System.getProperty("line.separator", "\n");
    }

    private String makeImportForRuleSchema(
            final BlancoVeeValidateClassStructure argStructure
    ) {
        String importString = "";
        String packageName = argStructure.getPackage();
        String simpleClassName = BlancoNameAdjuster.toClassName(argStructure.getValidator()) + "RuleSchema";
        String baseDir = argStructure.getBasedir();

        if (baseDir != null & baseDir.length() > 0) {
            baseDir += "/";
        }
        if (packageName != null & packageName.length() > 0) {
            packageName += "/";
        }
        importString = "import { " + simpleClassName + " } from \"" +
                baseDir + packageName.replace(".", "/") + simpleClassName + "\"";

        return importString;
    }
}
