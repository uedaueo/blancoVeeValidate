package blanco.veevalidate.valueobject;

import java.util.List;

/**
 * VeeValidateのクラスをあらわすバリューオブジェクトクラス。このクラスの設定情報をもとにクラスが自動生成されます。
 */
public class BlancoVeeValidateClassStructure {
    /**
     * フィールド名を指定します。必須項目です。
     *
     * フィールド: [name]。
     */
    private String fName;

    /**
     * パッケージ名を指定します。必須項目です。
     *
     * フィールド: [package]。
     */
    private String fPackage;

    /**
     * listClassが指定された場合に、プロパティ名として使われます。未指定の場合はnameをlowerCamelCaseに変換して使用します。
     *
     * フィールド: [classAlias]。
     */
    private String fClassAlias;

    /**
     * validatorのscheme名を格納します。
     *
     * フィールド: [validator]。
     */
    private String fValidator;

    /**
     * importを指定します。
     *
     * フィールド: [importList]。
     * デフォルト: [new java.util.ArrayList&lt;java.lang.String&gt;()]。
     */
    private List<String> fImportList = new java.util.ArrayList<java.lang.String>();

    /**
     * source コードの先頭に書かれるコード群です。
     *
     * フィールド: [headerList]。
     * デフォルト: [new java.util.ArrayList&lt;java.lang.String&gt;()]。
     */
    private List<String> fHeaderList = new java.util.ArrayList<java.lang.String>();

    /**
     * 本番時にファイルを配置する際のベースディレクトリ。主にTypeScriptのimport文生成時に使用する事を想定しています。
     *
     * フィールド: [basedir]。
     */
    private String fBasedir;

    /**
     * 本番時に実装クラスを配置する際のベースディレクトリ。主にTypeScriptのimport文生成時に使用する事を想定しています。
     *
     * フィールド: [impledir]。
     */
    private String fImpledir;

    /**
     * クラスの説明です。
     *
     * フィールド: [description]。
     */
    private String fDescription;

    /**
     * クラスの補助説明です。文字参照エンコード済みの値を格納してください。
     *
     * フィールド: [descriptionList]。
     * デフォルト: [new java.util.ArrayList&lt;java.lang.String&gt;()]。
     */
    private List<String> fDescriptionList = new java.util.ArrayList<java.lang.String>();

    /**
     * 定義書の種別。custom/builtin/config/message
     *
     * フィールド: [validatorKind]。
     * デフォルト: [&quot;builtin&quot;]。
     */
    private String fValidatorKind = "builtin";

    /**
     * メッセージの差し替え
     *
     * フィールド: [alterMessage]。
     * デフォルト: [false]。
     */
    private Boolean fAlterMessage = false;

    /**
     * 必須扱いとする。true の時のみ、Schemeクラスに computesRequired: boolean = true; を定義します。
     *
     * フィールド: [computesRequired]。
     * デフォルト: [false]。
     */
    private Boolean fComputesRequired = false;

    /**
     * フィールド名の名前変形をおこなうかどうか。
     *
     * フィールド: [adjustFieldName]。
     * デフォルト: [true]。
     */
    private Boolean fAdjustFieldName = true;

    /**
     * TypeScript 独自。blancoで一括生成されたクラスについて、import文を自動生成します。
     *
     * フィールド: [createImportList]。
     * デフォルト: [true]。
     */
    private Boolean fCreateImportList = true;

    /**
     * 継承するクラスを指定します。
     *
     * フィールド: [extends]。
     */
    private String fExtends;

    /**
     * フィールドを記憶するリストを指定します。
     *
     * フィールド: [fieldList]。
     * デフォルト: [new java.util.ArrayList&lt;blanco.veevalidate.valueobject.BlancoVeeValidateFieldStructure&gt;()]。
     */
    private List<BlancoVeeValidateFieldStructure> fFieldList = new java.util.ArrayList<blanco.veevalidate.valueobject.BlancoVeeValidateFieldStructure>();

    /**
     * ファイル説明
     *
     * フィールド: [fileDescription]。
     */
    private String fFileDescription;

    /**
     * 言語毎のメッセージを定義します。
     *
     * フィールド: [message]。
     * デフォルト: [new java.util.ArrayList&lt;&gt;()]。
     */
    private List<BlancoVeeValidateMessageStructure> fMessage = new java.util.ArrayList<>();

    /**
     * フィールド [name] の値を設定します。
     *
     * フィールドの説明: [フィールド名を指定します。必須項目です。]。
     *
     * @param argName フィールド[name]に設定する値。
     */
    public void setName(final String argName) {
        fName = argName;
    }

    /**
     * フィールド [name] の値を取得します。
     *
     * フィールドの説明: [フィールド名を指定します。必須項目です。]。
     *
     * @return フィールド[name]から取得した値。
     */
    public String getName() {
        return fName;
    }

    /**
     * フィールド [package] の値を設定します。
     *
     * フィールドの説明: [パッケージ名を指定します。必須項目です。]。
     *
     * @param argPackage フィールド[package]に設定する値。
     */
    public void setPackage(final String argPackage) {
        fPackage = argPackage;
    }

    /**
     * フィールド [package] の値を取得します。
     *
     * フィールドの説明: [パッケージ名を指定します。必須項目です。]。
     *
     * @return フィールド[package]から取得した値。
     */
    public String getPackage() {
        return fPackage;
    }

    /**
     * フィールド [classAlias] の値を設定します。
     *
     * フィールドの説明: [listClassが指定された場合に、プロパティ名として使われます。未指定の場合はnameをlowerCamelCaseに変換して使用します。]。
     *
     * @param argClassAlias フィールド[classAlias]に設定する値。
     */
    public void setClassAlias(final String argClassAlias) {
        fClassAlias = argClassAlias;
    }

    /**
     * フィールド [classAlias] の値を取得します。
     *
     * フィールドの説明: [listClassが指定された場合に、プロパティ名として使われます。未指定の場合はnameをlowerCamelCaseに変換して使用します。]。
     *
     * @return フィールド[classAlias]から取得した値。
     */
    public String getClassAlias() {
        return fClassAlias;
    }

    /**
     * フィールド [validator] の値を設定します。
     *
     * フィールドの説明: [validatorのscheme名を格納します。]。
     *
     * @param argValidator フィールド[validator]に設定する値。
     */
    public void setValidator(final String argValidator) {
        fValidator = argValidator;
    }

    /**
     * フィールド [validator] の値を取得します。
     *
     * フィールドの説明: [validatorのscheme名を格納します。]。
     *
     * @return フィールド[validator]から取得した値。
     */
    public String getValidator() {
        return fValidator;
    }

    /**
     * フィールド [importList] の値を設定します。
     *
     * フィールドの説明: [importを指定します。]。
     *
     * @param argImportList フィールド[importList]に設定する値。
     */
    public void setImportList(final List<String> argImportList) {
        fImportList = argImportList;
    }

    /**
     * フィールド [importList] の値を取得します。
     *
     * フィールドの説明: [importを指定します。]。
     * デフォルト: [new java.util.ArrayList&lt;java.lang.String&gt;()]。
     *
     * @return フィールド[importList]から取得した値。
     */
    public List<String> getImportList() {
        return fImportList;
    }

    /**
     * フィールド [headerList] の値を設定します。
     *
     * フィールドの説明: [source コードの先頭に書かれるコード群です。]。
     *
     * @param argHeaderList フィールド[headerList]に設定する値。
     */
    public void setHeaderList(final List<String> argHeaderList) {
        fHeaderList = argHeaderList;
    }

    /**
     * フィールド [headerList] の値を取得します。
     *
     * フィールドの説明: [source コードの先頭に書かれるコード群です。]。
     * デフォルト: [new java.util.ArrayList&lt;java.lang.String&gt;()]。
     *
     * @return フィールド[headerList]から取得した値。
     */
    public List<String> getHeaderList() {
        return fHeaderList;
    }

    /**
     * フィールド [basedir] の値を設定します。
     *
     * フィールドの説明: [本番時にファイルを配置する際のベースディレクトリ。主にTypeScriptのimport文生成時に使用する事を想定しています。]。
     *
     * @param argBasedir フィールド[basedir]に設定する値。
     */
    public void setBasedir(final String argBasedir) {
        fBasedir = argBasedir;
    }

    /**
     * フィールド [basedir] の値を取得します。
     *
     * フィールドの説明: [本番時にファイルを配置する際のベースディレクトリ。主にTypeScriptのimport文生成時に使用する事を想定しています。]。
     *
     * @return フィールド[basedir]から取得した値。
     */
    public String getBasedir() {
        return fBasedir;
    }

    /**
     * フィールド [impledir] の値を設定します。
     *
     * フィールドの説明: [本番時に実装クラスを配置する際のベースディレクトリ。主にTypeScriptのimport文生成時に使用する事を想定しています。]。
     *
     * @param argImpledir フィールド[impledir]に設定する値。
     */
    public void setImpledir(final String argImpledir) {
        fImpledir = argImpledir;
    }

    /**
     * フィールド [impledir] の値を取得します。
     *
     * フィールドの説明: [本番時に実装クラスを配置する際のベースディレクトリ。主にTypeScriptのimport文生成時に使用する事を想定しています。]。
     *
     * @return フィールド[impledir]から取得した値。
     */
    public String getImpledir() {
        return fImpledir;
    }

    /**
     * フィールド [description] の値を設定します。
     *
     * フィールドの説明: [クラスの説明です。]。
     *
     * @param argDescription フィールド[description]に設定する値。
     */
    public void setDescription(final String argDescription) {
        fDescription = argDescription;
    }

    /**
     * フィールド [description] の値を取得します。
     *
     * フィールドの説明: [クラスの説明です。]。
     *
     * @return フィールド[description]から取得した値。
     */
    public String getDescription() {
        return fDescription;
    }

    /**
     * フィールド [descriptionList] の値を設定します。
     *
     * フィールドの説明: [クラスの補助説明です。文字参照エンコード済みの値を格納してください。]。
     *
     * @param argDescriptionList フィールド[descriptionList]に設定する値。
     */
    public void setDescriptionList(final List<String> argDescriptionList) {
        fDescriptionList = argDescriptionList;
    }

    /**
     * フィールド [descriptionList] の値を取得します。
     *
     * フィールドの説明: [クラスの補助説明です。文字参照エンコード済みの値を格納してください。]。
     * デフォルト: [new java.util.ArrayList&lt;java.lang.String&gt;()]。
     *
     * @return フィールド[descriptionList]から取得した値。
     */
    public List<String> getDescriptionList() {
        return fDescriptionList;
    }

    /**
     * フィールド [validatorKind] の値を設定します。
     *
     * フィールドの説明: [定義書の種別。custom/builtin/config/message]。
     *
     * @param argValidatorKind フィールド[validatorKind]に設定する値。
     */
    public void setValidatorKind(final String argValidatorKind) {
        fValidatorKind = argValidatorKind;
    }

    /**
     * フィールド [validatorKind] の値を取得します。
     *
     * フィールドの説明: [定義書の種別。custom/builtin/config/message]。
     * デフォルト: [&quot;builtin&quot;]。
     *
     * @return フィールド[validatorKind]から取得した値。
     */
    public String getValidatorKind() {
        return fValidatorKind;
    }

    /**
     * フィールド [alterMessage] の値を設定します。
     *
     * フィールドの説明: [メッセージの差し替え]。
     *
     * @param argAlterMessage フィールド[alterMessage]に設定する値。
     */
    public void setAlterMessage(final Boolean argAlterMessage) {
        fAlterMessage = argAlterMessage;
    }

    /**
     * フィールド [alterMessage] の値を取得します。
     *
     * フィールドの説明: [メッセージの差し替え]。
     * デフォルト: [false]。
     *
     * @return フィールド[alterMessage]から取得した値。
     */
    public Boolean getAlterMessage() {
        return fAlterMessage;
    }

    /**
     * フィールド [computesRequired] の値を設定します。
     *
     * フィールドの説明: [必須扱いとする。true の時のみ、Schemeクラスに computesRequired: boolean = true; を定義します。]。
     *
     * @param argComputesRequired フィールド[computesRequired]に設定する値。
     */
    public void setComputesRequired(final Boolean argComputesRequired) {
        fComputesRequired = argComputesRequired;
    }

    /**
     * フィールド [computesRequired] の値を取得します。
     *
     * フィールドの説明: [必須扱いとする。true の時のみ、Schemeクラスに computesRequired: boolean = true; を定義します。]。
     * デフォルト: [false]。
     *
     * @return フィールド[computesRequired]から取得した値。
     */
    public Boolean getComputesRequired() {
        return fComputesRequired;
    }

    /**
     * フィールド [adjustFieldName] の値を設定します。
     *
     * フィールドの説明: [フィールド名の名前変形をおこなうかどうか。]。
     *
     * @param argAdjustFieldName フィールド[adjustFieldName]に設定する値。
     */
    public void setAdjustFieldName(final Boolean argAdjustFieldName) {
        fAdjustFieldName = argAdjustFieldName;
    }

    /**
     * フィールド [adjustFieldName] の値を取得します。
     *
     * フィールドの説明: [フィールド名の名前変形をおこなうかどうか。]。
     * デフォルト: [true]。
     *
     * @return フィールド[adjustFieldName]から取得した値。
     */
    public Boolean getAdjustFieldName() {
        return fAdjustFieldName;
    }

    /**
     * フィールド [createImportList] の値を設定します。
     *
     * フィールドの説明: [TypeScript 独自。blancoで一括生成されたクラスについて、import文を自動生成します。]。
     *
     * @param argCreateImportList フィールド[createImportList]に設定する値。
     */
    public void setCreateImportList(final Boolean argCreateImportList) {
        fCreateImportList = argCreateImportList;
    }

    /**
     * フィールド [createImportList] の値を取得します。
     *
     * フィールドの説明: [TypeScript 独自。blancoで一括生成されたクラスについて、import文を自動生成します。]。
     * デフォルト: [true]。
     *
     * @return フィールド[createImportList]から取得した値。
     */
    public Boolean getCreateImportList() {
        return fCreateImportList;
    }

    /**
     * フィールド [extends] の値を設定します。
     *
     * フィールドの説明: [継承するクラスを指定します。]。
     *
     * @param argExtends フィールド[extends]に設定する値。
     */
    public void setExtends(final String argExtends) {
        fExtends = argExtends;
    }

    /**
     * フィールド [extends] の値を取得します。
     *
     * フィールドの説明: [継承するクラスを指定します。]。
     *
     * @return フィールド[extends]から取得した値。
     */
    public String getExtends() {
        return fExtends;
    }

    /**
     * フィールド [fieldList] の値を設定します。
     *
     * フィールドの説明: [フィールドを記憶するリストを指定します。]。
     *
     * @param argFieldList フィールド[fieldList]に設定する値。
     */
    public void setFieldList(final List<BlancoVeeValidateFieldStructure> argFieldList) {
        fFieldList = argFieldList;
    }

    /**
     * フィールド [fieldList] の値を取得します。
     *
     * フィールドの説明: [フィールドを記憶するリストを指定します。]。
     * デフォルト: [new java.util.ArrayList&lt;blanco.veevalidate.valueobject.BlancoVeeValidateFieldStructure&gt;()]。
     *
     * @return フィールド[fieldList]から取得した値。
     */
    public List<BlancoVeeValidateFieldStructure> getFieldList() {
        return fFieldList;
    }

    /**
     * フィールド [fileDescription] の値を設定します。
     *
     * フィールドの説明: [ファイル説明]。
     *
     * @param argFileDescription フィールド[fileDescription]に設定する値。
     */
    public void setFileDescription(final String argFileDescription) {
        fFileDescription = argFileDescription;
    }

    /**
     * フィールド [fileDescription] の値を取得します。
     *
     * フィールドの説明: [ファイル説明]。
     *
     * @return フィールド[fileDescription]から取得した値。
     */
    public String getFileDescription() {
        return fFileDescription;
    }

    /**
     * フィールド [message] の値を設定します。
     *
     * フィールドの説明: [言語毎のメッセージを定義します。]。
     *
     * @param argMessage フィールド[message]に設定する値。
     */
    public void setMessage(final List<BlancoVeeValidateMessageStructure> argMessage) {
        fMessage = argMessage;
    }

    /**
     * フィールド [message] の値を取得します。
     *
     * フィールドの説明: [言語毎のメッセージを定義します。]。
     * デフォルト: [new java.util.ArrayList&lt;&gt;()]。
     *
     * @return フィールド[message]から取得した値。
     */
    public List<BlancoVeeValidateMessageStructure> getMessage() {
        return fMessage;
    }

    /**
     * Gets the string representation of this value object.
     *
     * <P>Precautions for use</P>
     * <UL>
     * <LI>Only the shallow range of the object will be subject to the stringification process.
     * <LI>Do not use this method if the object has a circular reference.
     * </UL>
     *
     * @return String representation of a value object.
     */
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("blanco.veevalidate.valueobject.BlancoVeeValidateClassStructure[");
        buf.append("name=" + fName);
        buf.append(",package=" + fPackage);
        buf.append(",classAlias=" + fClassAlias);
        buf.append(",validator=" + fValidator);
        buf.append(",importList=" + fImportList);
        buf.append(",headerList=" + fHeaderList);
        buf.append(",basedir=" + fBasedir);
        buf.append(",impledir=" + fImpledir);
        buf.append(",description=" + fDescription);
        buf.append(",descriptionList=" + fDescriptionList);
        buf.append(",validatorKind=" + fValidatorKind);
        buf.append(",alterMessage=" + fAlterMessage);
        buf.append(",computesRequired=" + fComputesRequired);
        buf.append(",adjustFieldName=" + fAdjustFieldName);
        buf.append(",createImportList=" + fCreateImportList);
        buf.append(",extends=" + fExtends);
        buf.append(",fieldList=" + fFieldList);
        buf.append(",fileDescription=" + fFileDescription);
        buf.append(",message=" + fMessage);
        buf.append("]");
        return buf.toString();
    }

    /**
     * このバリューオブジェクトを指定のターゲットに複写します。
     *
     * <P>使用上の注意</P>
     * <UL>
     * <LI>オブジェクトのシャロー範囲のみ複写処理対象となります。
     * <LI>オブジェクトが循環参照している場合には、このメソッドは使わないでください。
     * </UL>
     *
     * @param target target value object.
     */
    public void copyTo(final BlancoVeeValidateClassStructure target) {
        if (target == null) {
            throw new IllegalArgumentException("Bug: BlancoVeeValidateClassStructure#copyTo(target): argument 'target' is null");
        }

        // No needs to copy parent class.

        // Name: fName
        // Type: java.lang.String
        target.fName = this.fName;
        // Name: fPackage
        // Type: java.lang.String
        target.fPackage = this.fPackage;
        // Name: fClassAlias
        // Type: java.lang.String
        target.fClassAlias = this.fClassAlias;
        // Name: fValidator
        // Type: java.lang.String
        target.fValidator = this.fValidator;
        // Name: fImportList
        // Type: java.util.List
        // フィールド[fImportList]はサポート外の型[java.util.Listjava.lang.String]です。
        // Name: fHeaderList
        // Type: java.util.List
        // フィールド[fHeaderList]はサポート外の型[java.util.Listjava.lang.String]です。
        // Name: fBasedir
        // Type: java.lang.String
        target.fBasedir = this.fBasedir;
        // Name: fImpledir
        // Type: java.lang.String
        target.fImpledir = this.fImpledir;
        // Name: fDescription
        // Type: java.lang.String
        target.fDescription = this.fDescription;
        // Name: fDescriptionList
        // Type: java.util.List
        // フィールド[fDescriptionList]はサポート外の型[java.util.Listjava.lang.String]です。
        // Name: fValidatorKind
        // Type: java.lang.String
        target.fValidatorKind = this.fValidatorKind;
        // Name: fAlterMessage
        // Type: java.lang.Boolean
        target.fAlterMessage = this.fAlterMessage;
        // Name: fComputesRequired
        // Type: java.lang.Boolean
        target.fComputesRequired = this.fComputesRequired;
        // Name: fAdjustFieldName
        // Type: java.lang.Boolean
        target.fAdjustFieldName = this.fAdjustFieldName;
        // Name: fCreateImportList
        // Type: java.lang.Boolean
        target.fCreateImportList = this.fCreateImportList;
        // Name: fExtends
        // Type: java.lang.String
        target.fExtends = this.fExtends;
        // Name: fFieldList
        // Type: java.util.List
        // フィールド[fFieldList]はサポート外の型[java.util.Listblanco.veevalidate.valueobject.BlancoVeeValidateFieldStructure]です。
        // Name: fFileDescription
        // Type: java.lang.String
        target.fFileDescription = this.fFileDescription;
        // Name: fMessage
        // Type: java.util.List
        // フィールド[fMessage]はサポート外の型[java.util.Listblanco.veevalidate.valueobject.BlancoVeeValidateMessageStructure]です。
    }
}
