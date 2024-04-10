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
 * A class that auto-generates TypeScript source code from intermediate XML files for value objects.
 *
 * This is one of the main classes of BlancoValueObjectTs.
 *
 * @author IGA Tosiki
 * @author tueda
 */
public class BlancoVeeValidateXml2TypeScriptClass {
    /**
     * A message.
     */
    private final BlancoVeeValidateMessage fMsg = new BlancoVeeValidateMessage();

    /**
     * Resource bundle object for blancoValueObject.
     */
    private final BlancoVeeValidateResourceBundle fBundle = new BlancoVeeValidateResourceBundle();

    /**
     * A programming language expected for the input sheet.
     */
    private int fSheetLang = BlancoCgSupportedLang.PHP;

    public void setSheetLang(final int argSheetLang) {
        fSheetLang = argSheetLang;
    }

    /**
     * Style of the source code generation destination directory
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
     * A factory for blancoCg to be used internally.
     */
    private BlancoCgObjectFactory fCgFactory = null;

    /**
     * Source file information for blancoCg to be used internally.
     */
    private BlancoCgSourceFile fCgSourceFile = null;

    /**
     * Class information for blancoCg to be used internally.
     */
    private BlancoCgClass fCgClass = null;

    /**
     * Class information for blancoCg to be used internally.
     */
    private BlancoCgClass fCgModalClass = null;

    /**
     * Interface information for blancoCg to be used internally.
     */
    private BlancoCgInterface fCgMetaInfoInterface = null;

    /**
     * Interface information for blancoCg to be used internally.
     */
    private BlancoCgInterface fCgConfigInterface = null;

    /**
     * Character encoding of auto-generated source files.
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
         * Next, generates the message the definition files for each language.
         */
        generateValidationMessages(argClassStructures, argDirectoryTarget);


        /*
         * Then, generates the configuration class file.
         */
        generateConfigClass(argClassStructures, argDirectoryTarget);

    }

    /**
     * Generates a custom message.
     *
     * @param argClassStructures
     * @param argDirectoryTarget
     * @throws IOException
     */
    private void generateValidationMessages(
            final List<BlancoVeeValidateClassStructure> argClassStructures,
            final File argDirectoryTarget) throws IOException {
        /*
         * The output directory will be in the format specified by the targetStyle argument of the ant task.
         * For compatibility, the output directory will be blanco/main if it is not specified.
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
         * First, searches for the message class definition.
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

            // Gets an instance of BlancoCgObjectFactory class.
            fCgFactory = BlancoCgObjectFactory.getInstance();
            fCgSourceFile = fCgFactory.createSourceFile(packageName, "This source code was created by blanco Framework.");
            fCgSourceFile.setEncoding(fEncoding);
            fCgSourceFile.setTabs(this.getTabs());

            // Creates a class.
            fCgClass = fCgFactory.createClass(simpleClassName, fBundle.getXml2sourceFileValidationMessage());
            fCgSourceFile.getClassList().add(fCgClass);
            fCgClass.setAccess("public");

            // Supports inheritance.
            if (BlancoStringUtil.null2Blank(messageStructure.getExtends())
                    .length() > 0) {
                fCgClass.getExtendClassList().add(
                        fCgFactory.createType(messageStructure.getExtends()));
            }

            // Defines the message as a field.
            generateValidationMessageLang(lang, argClassStructures);

            /* In TypeScript, sets the header instead of import */
            for (String header : messageStructure.getHeaderList()) {
                fCgSourceFile.getHeaderList().add(header);
            }

            // Auto-generates the actual source code based on the collected information.
            BlancoCgTransformerFactory.getTsSourceTransformer().transform(
                    fCgSourceFile, fileBlancoMain);
        }
    }

    /**
     * Searches for the class name that defines the message.
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
     * Generates a custom message for each language.
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
                        // Adds double-quotes.
                        "\"" + BlancoJavaSourceUtil.escapeStringAsJavaSource(message.getMessage()) + "\"");
                field.setAccess("public");
                field.setNotnull(true);

                field.getLangDoc().getDescriptionList().add(
                        structure.getValidator() + ": " + BlancoJavaSourceUtil.escapeStringAsJavaSource(message.getMessage())
                );
                for (BlancoVeeValidateFieldStructure param: structure.getFieldList()) {
                    /* Describes only the first line of the parameter. */
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
     * Auto-generates source code from a given class information value object.
     *
     * @param argClassStructures
     *            Class information.
     * @param argDirectoryTarget
     *            Output directory for TypeScript source code.
     * @throws IOException
     *             If an I/O exception occurs.
     */
    public void generateConfigClass(
            final List<BlancoVeeValidateClassStructure> argClassStructures,
            final File argDirectoryTarget) throws IOException {
        /*
         * The output directory will be in the format specified by the targetStyle argument of the ant task.
         * For compatibility, the output directory will be blanco/main if it is not specified.
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
         * The first step is to seach for the Config class definition.
         */
        BlancoVeeValidateClassStructure configStructure = searchValidateClassByKind(argClassStructures, "config");
        if (configStructure == null) {
            throw new IllegalArgumentException(this.fBundle.getXml2sourceFileErr007());
        }

        // Gets an instance of BlancoCgObjectFactory class.
        fCgFactory = BlancoCgObjectFactory.getInstance();

        fCgSourceFile = fCgFactory.createSourceFile(configStructure
                .getPackage(), "This source code was created by blanco Framework.");
        fCgSourceFile.setEncoding(fEncoding);
        fCgSourceFile.setTabs(this.getTabs());

        // Creates a class.
        fCgClass = fCgFactory.createClass(configStructure.getName(), this.fBundle.getXml2sourceFileValidateInit());
        fCgSourceFile.getClassList().add(fCgClass);
        fCgClass.setAccess("public");

        // Inheritance
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

        // Sets the JavaDoc for the class.
        fCgClass.setDescription(configStructure.getDescription());
        for (String line : configStructure.getDescriptionList()) {
            fCgClass.getLangDoc().getDescriptionList().add(line);
        }

        // Generate i18n field
        BlancoCgField cgI18nField = fCgFactory.createField("i18n", "I18n", fBundle.getXml2sourceFileValidateInitI18n());
        fCgClass.getFieldList().add(cgI18nField);
        cgI18nField.getType().setGenerics("LocaleMessages<DapandaI18nResources>, any, any, string, false");
        cgI18nField.setAccess("public");
        cgI18nField.setStatic(true);
        cgI18nField.setNotnull(false);

        // Generates the setup method.
        buildSetupMethod(argClassStructures, configStructure);

        // Generate FieldValidationMetaInfo
        buildFieldValidationMetaInfo(configStructure);

        // Generate VeeValidateConfig
        buildVeeValidateConfig(configStructure);

        // Generate types and functions
        fCgModalClass = fCgFactory.createClass("ModalClass", null);
        fCgSourceFile.getClassList().add(fCgModalClass);
        fCgModalClass.setNoClassDeclare(true);

        // Generate types defined in VeeValidate
        buildTypes(configStructure);

        // Generate functions
        buildInterporator(configStructure);
        buildValidateConfig(configStructure);

        /* In TypeScript, sets the header instead of import. */
        for (String header : configStructure.getHeaderList()) {
            fCgSourceFile.getHeaderList().add(header);
        }

        // Auto-generates the actual source code based on the collected information.
        BlancoCgTransformerFactory.getTsSourceTransformer().transform(
                fCgSourceFile, fileBlancoMain);
    }

    private void buildSetupMethod(
            final List<BlancoVeeValidateClassStructure> argClassStructures,
            final BlancoVeeValidateClassStructure argConfigStructure
    ) {
        // Generates a getter method for each field.
        final BlancoCgMethod method = fCgFactory.createMethod(BlancoVeeValidateConstants.INIT_METHOD,
                fBundle.getXml2sourceFileValidateInitMethod());
        fCgClass.getMethodList().add(method);
        method.setNotnull(true);
        method.setAccess("public");
        method.setStatic(true);

        method.getLineList().add("configure(validateConfig);");
        method.getLineList().add("");
        method.getLineList().add("/* builtin rules */");

        List<String> builtinNormal = new ArrayList<>();

        // First, sets up the builtin rule.
        for (BlancoVeeValidateClassStructure structure : argClassStructures) {
            if ("builtin".equalsIgnoreCase(structure.getValidatorKind())) {
                String validator = structure.getValidator();
                method.getLineList().add("defineRule(\"" +
                        structure.getName() + "\", " +
                        validator + ");");
                builtinNormal.add(validator);
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
            builtinImport.append("} from \"@vee-validate/rules\"");
            argConfigStructure.getHeaderList().add(builtinImport.toString());
        }

        method.getLineList().add("");
        method.getLineList().add("/* custom rules */");

        List<String> customImport = new ArrayList<>();
        // Then, sets up the custom rule.
        for (BlancoVeeValidateClassStructure structure : argClassStructures) {
            if ("custom".equalsIgnoreCase(structure.getValidatorKind())) {
                String validator = BlancoNameAdjuster.toParameterName(structure.getValidator()) + "Validator";
                method.getLineList().add("defineRule(\"" +
                        structure.getName() + "\", " +
                        validator + ");");
                customImport.add(this.makeImportForRuleSchema(structure));
            }
        }
        if (customImport.size() > 0) {
            argConfigStructure.getHeaderList().addAll(customImport);
        }
    }


    private void buildFieldValidationMetaInfo(
            final BlancoVeeValidateClassStructure argConfigStructure
    ) {
        // generate FieldValidationMetaInfo interface
        fCgMetaInfoInterface = fCgFactory.createInterface("FieldValidationMetaInfo", fBundle.getXml2sourceFileValidateRedefineTypes());
        fCgSourceFile.getInterfaceList().add(fCgMetaInfoInterface);
        fCgMetaInfoInterface.setAccess("public");

        // field: string
        final BlancoCgField metaInfoField = fCgFactory.createField("field", "string", fBundle.getXml2sourceFileValidationMetaInfoField());
        fCgMetaInfoInterface.getFieldList().add(metaInfoField);
        metaInfoField.setNotnull(true);
        metaInfoField.setAccess("");

        // value: unknown
        final BlancoCgField metaInfoValue = fCgFactory.createField("value", "unknown", fBundle.getXml2sourceFileValidationMetaInfoValue());
        fCgMetaInfoInterface.getFieldList().add(metaInfoValue);
        metaInfoValue.setNotnull(true);
        metaInfoValue.setAccess("");

        // form: Record<string, unknown>
        final BlancoCgField metaInfoForm = fCgFactory.createField("form", "Record", fBundle.getXml2sourceFileValidationMetaInfoValue());
        fCgMetaInfoInterface.getFieldList().add(metaInfoForm);
        metaInfoForm.getType().setGenerics("string, unknown");
        metaInfoForm.setNotnull(true);
        metaInfoForm.setAccess("");

        // generate MetaInfoRule interface
        final BlancoCgInterface metaInfoRuleInterface = fCgFactory.createInterface("MetaInfoRule", fBundle.getXml2sourceFileValidationMetaInfoRule());
        fCgSourceFile.getInterfaceList().add(metaInfoRuleInterface);
        metaInfoRuleInterface.setAccess("");
        {
            final BlancoCgField metaInfoRuleName = fCgFactory.createField("name", "string", fBundle.getXml2sourceFileValidationMetaInfoRuleName());
            metaInfoRuleInterface.getFieldList().add(metaInfoRuleName);
            metaInfoRuleName.setNotnull(true);
            metaInfoRuleName.setAccess("");

            final BlancoCgField metaInfoRuleParams = fCgFactory.createField("params", "Record<string, unknown> | Array<unknown>", fBundle.getXml2sourceFileValidationMetaInfoRuleParams());
            metaInfoRuleInterface.getFieldList().add(metaInfoRuleParams);
            metaInfoRuleParams.setNotnull(false);
            metaInfoRuleParams.setAccess("");
        }

        // rule?: MetaInfoRule
        final BlancoCgField metaInfoRule = fCgFactory.createField("rule", "MetaInfoRule", fBundle.getXml2sourceFileValidationMetaInfoRule());
        fCgMetaInfoInterface.getFieldList().add(metaInfoRule);
        metaInfoRule.setNotnull(false);
        metaInfoRule.setAccess("");
    }

    private void buildVeeValidateConfig(
            final BlancoVeeValidateClassStructure argConfigStructure
    ) {
        // generate VeeValidateConfig interface
        fCgConfigInterface = fCgFactory.createInterface("VeeValidateConfig", fBundle.getXml2sourceFileValidateRedefineTypes());
        fCgSourceFile.getInterfaceList().add(fCgConfigInterface);
        fCgMetaInfoInterface.setAccess("public");

        // bails: boolean;
        final BlancoCgField cgBails = fCgFactory.createField("bails", "boolean", null);
        fCgConfigInterface.getFieldList().add(cgBails);
        cgBails.setNotnull(true);
        cgBails.setAccess("");
        // generateMessage: ValidationMessageGenerator;
        final BlancoCgField cgGenerateMessage = fCgFactory.createField("generateMessage", "ValidationMessageGenerator", null);
        fCgConfigInterface.getFieldList().add(cgGenerateMessage);
        cgGenerateMessage.setNotnull(true);
        cgGenerateMessage.setAccess("");
        // validateOnInput: boolean;
        final BlancoCgField cgValidateOnInput = fCgFactory.createField("validateOnInput", "boolean", null);
        fCgConfigInterface.getFieldList().add(cgValidateOnInput);
        cgValidateOnInput.setNotnull(true);
        cgValidateOnInput.setAccess("");
        // validateOnChange: boolean;
        final BlancoCgField cgValidateOnChange = fCgFactory.createField("validateOnChange", "boolean", null);
        fCgConfigInterface.getFieldList().add(cgValidateOnChange);
        cgValidateOnChange.setNotnull(true);
        cgValidateOnChange.setAccess("");
        // validateOnBlur: boolean;
        final BlancoCgField cgValidateOnBlur = fCgFactory.createField("validateOnBlur", "boolean", null);
        fCgConfigInterface.getFieldList().add(cgValidateOnBlur);
        cgValidateOnBlur.setNotnull(true);
        cgValidateOnBlur.setAccess("");
        // validateOnModelUpdate: boolean;
        final BlancoCgField cgValidateOnModelUpdate = fCgFactory.createField("validateOnModelUpdate", "boolean", null);
        fCgConfigInterface.getFieldList().add(cgValidateOnModelUpdate);
        cgValidateOnModelUpdate.setNotnull(true);
        cgValidateOnModelUpdate.setAccess("");
    }

    private void buildTypes(
            final BlancoVeeValidateClassStructure argConfigStructure
    ) {
        // ValidationRuleFunction
        List<String> plainTextList = fCgModalClass.getPlainTextList();
        plainTextList.add("/*");
        plainTextList.add(" * " + fBundle.getXml2sourceFileValidateRedefineTypes());
        plainTextList.add(" */");

        plainTextList.add("export declare type ValidationRuleFunction<TValue = unknown, TParams = unknown[] | Record<string, unknown>> = (value: TValue, params: TParams, ctx: FieldValidationMetaInfo) => boolean | string | Promise<boolean | string>");
        plainTextList.add("");
        plainTextList.add("export declare type SimpleValidationRuleFunction<TValue = unknown, TParams = unknown[] | Record<string, unknown>> = (value: TValue, params: TParams) => boolean | string | Promise<boolean | string>");
        plainTextList.add("");
        plainTextList.add("export declare type ValidationMessageGenerator = (ctx: FieldValidationMetaInfo) => string");
    }

    private void buildInterporator(
            final BlancoVeeValidateClassStructure argConfigStructure
    ) {
        // interpolator
        final BlancoCgMethod cgInterpolator = fCgFactory.createMethod("interpolate", fBundle.getXml2sourceFileValidationInterporatorLangdoc());
        fCgModalClass.getMethodList().add(cgInterpolator);
        cgInterpolator.setNotnull(true);
        cgInterpolator.setAccess("export function");
        BlancoCgReturn cgReturn = fCgFactory.createReturn("string", fBundle.getXml2sourceFileValidationInterporatorReturn());
        cgInterpolator.setReturn(cgReturn);

        // template: string
        final BlancoCgParameter cgTemplate = fCgFactory.createParameter("template", "string", fBundle.getXml2sourceFileValidationInterporatorParam01());
        cgTemplate.setNotnull(true);
        cgInterpolator.getParameterList().add(cgTemplate);

        // values: Record<string, any>
        final BlancoCgParameter cgValues = fCgFactory.createParameter("values", "Record", fBundle.getXml2sourceFileValidationInterporatorParam02());
        cgInterpolator.getParameterList().add(cgValues);
        cgValues.getType().setGenerics("string, any");
        cgValues.setNotnull(true);

        List<String> lines = cgInterpolator.getLineList();
        lines.add("return template.replace(/(\\d:)?{([^}]+)}/g, function (_, param, placeholder): string {");
        lines.add("if (!param || !values.params) {");
        lines.add("return placeholder in values ? values[placeholder] : values.params && placeholder in values.params ? values.params[placeholder] : `{${placeholder}}`;");
        lines.add("}");
        lines.add("");
        lines.add("// Handles extended object params format");
        lines.add("if (!Array.isArray(values.params)) {");
        lines.add("return placeholder in values.params ? values.params[placeholder] : `{${placeholder}}`;");
        lines.add("}");

        lines.add("// Extended Params exit in the format of `paramIndex:{paramName}` where the index is optional");
        lines.add("const paramIndex = Number(param.replace(':', ''));");
        lines.add("");
        lines.add("return paramIndex in values.params ? values.params[paramIndex] : `${param}{${placeholder}}`;");
        lines.add("});");

    }

    private void buildValidateConfig(
            final BlancoVeeValidateClassStructure argConfigStructure
    ) {
        // validateConfig: Partial<VeeValidateConfig>
        BlancoCgField cgValidateConfig = fCgFactory.createField("validateConfig", "Partial", fBundle.getXml2sourceFileValidationConfigOptions());
        fCgModalClass.getFieldList().add(cgValidateConfig);
        cgValidateConfig.getType().setGenerics("VeeValidateConfig");
        cgValidateConfig.setNotnull(true);
        cgValidateConfig.setAccess("export const");

        String validateConfigClass = fCgClass.getName();

        StringBuffer sb = new StringBuffer();
        sb.append("{" + this.getLineSeparator());
        sb.append(this.getTabSpace(1) + "generateMessage: (ctx) => {" + this.getLineSeparator());
        sb.append(this.getTabSpace(2) + "const {field, rule, form} = ctx;" + this.getLineSeparator());

        sb.append(this.getTabSpace(2) + "if (!" + validateConfigClass + ".i18n) {" + this.getLineSeparator());
        sb.append(this.getTabSpace(3) + "return field;" + this.getLineSeparator());
        sb.append(this.getTabSpace(2) + "}" + this.getLineSeparator());

        sb.append(this.getTabSpace(2) + "const fieldName = "+ validateConfigClass + ".i18n.global.t(field);" + this.getLineSeparator());
        sb.append(this.getTabSpace(2)  + "if (rule === undefined) {" + this.getLineSeparator());
        sb.append(this.getTabSpace(3) + "return field;" + this.getLineSeparator());
        sb.append(this.getTabSpace(2) + "}" + this.getLineSeparator());
        sb.append(this.getLineSeparator());

        sb.append(this.getTabSpace(2) + "/*" + this.getLineSeparator());
        sb.append(this.getTabSpace(2) + " * we get raw messages from dictionary and pass it to interpolator" + this.getLineSeparator());
        sb.append(this.getTabSpace(2) + " */" + this.getLineSeparator());
        sb.append(this.getTabSpace(2) + "const localeSettings = useLocaleSettingStore();" + this.getLineSeparator());
        sb.append(this.getTabSpace(2) + "const localeMessages = " + validateConfigClass + ".i18n.global.getLocaleMessage(localeSettings.lang) as LocaleMessageDictionary<DapandaI18nResources>;" + this.getLineSeparator());
        sb.append(this.getTabSpace(2) + "if (localeMessages === undefined) {" + this.getLineSeparator());
        sb.append(this.getTabSpace(3) + "return field;" + this.getLineSeparator());
        sb.append(this.getTabSpace(2) + "}" + this.getLineSeparator());
        sb.append(this.getTabSpace(2) + "let message = (localeMessages.validations as LocaleMessageDictionary<DapandaI18nResources>)[rule.name] as string;" + this.getLineSeparator());
        sb.append(this.getLineSeparator());

        sb.append(this.getTabSpace(2) + "if (message === undefined) {" + this.getLineSeparator());
        sb.append(this.getTabSpace(3) + "message = (localeMessages.validations as LocaleMessageDictionary<DapandaI18nResources>)[\"_default\"] as string;" + this.getLineSeparator());
        sb.append(this.getTabSpace(2) + "}" + this.getLineSeparator());
        sb.append(this.getTabSpace(2) + "return interpolate(message, {...form, field: fieldName, params: rule.params});" + this.getLineSeparator());
        sb.append(this.getTabSpace(1) + "}" + this.getLineSeparator());
        sb.append("}" + this.getLineSeparator());

        cgValidateConfig.setDefault(sb.toString());
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
        String simpleClassName = BlancoNameAdjuster.toClassName(argStructure.getValidator()) + "Validator";
        String baseDir = argStructure.getBasedir();
        String impleDir = argStructure.getImpledir();

        if (baseDir != null & baseDir.length() > 0) {
            baseDir += "/";
        }
        if (impleDir != null & impleDir.length() > 0) {
            impleDir += "/";
        }

        if (packageName != null & packageName.length() > 0) {
            packageName += "/";
        }
        importString = "import { " + BlancoNameAdjuster.toParameterName(simpleClassName) + " } from \"" +
                impleDir + packageName.replace(".", "/") + simpleClassName + "\"";

        return importString;
    }
}
