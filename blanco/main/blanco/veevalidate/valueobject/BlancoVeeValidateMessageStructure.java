package blanco.veevalidate.valueobject;

/**
 * validateエラーメッセージを定義するバリューオブジェクトクラス。このクラスの設定情報をもとにフィールドとセッター・ゲッターが自動生成されます。
 */
public class BlancoVeeValidateMessageStructure {
    /**
     * 項目番号。省略可能です。
     *
     * フィールド: [no]。
     */
    private String fNo;

    /**
     * メッセージを設定します。必須項目です。
     *
     * フィールド: [message]。
     */
    private String fMessage;

    /**
     * 言語を指定します。必須項目です。
     *
     * フィールド: [lang]。
     */
    private String fLang;

    /**
     * フィールド [no] の値を設定します。
     *
     * フィールドの説明: [項目番号。省略可能です。]。
     *
     * @param argNo フィールド[no]に設定する値。
     */
    public void setNo(final String argNo) {
        fNo = argNo;
    }

    /**
     * フィールド [no] の値を取得します。
     *
     * フィールドの説明: [項目番号。省略可能です。]。
     *
     * @return フィールド[no]から取得した値。
     */
    public String getNo() {
        return fNo;
    }

    /**
     * フィールド [message] の値を設定します。
     *
     * フィールドの説明: [メッセージを設定します。必須項目です。]。
     *
     * @param argMessage フィールド[message]に設定する値。
     */
    public void setMessage(final String argMessage) {
        fMessage = argMessage;
    }

    /**
     * フィールド [message] の値を取得します。
     *
     * フィールドの説明: [メッセージを設定します。必須項目です。]。
     *
     * @return フィールド[message]から取得した値。
     */
    public String getMessage() {
        return fMessage;
    }

    /**
     * フィールド [lang] の値を設定します。
     *
     * フィールドの説明: [言語を指定します。必須項目です。]。
     *
     * @param argLang フィールド[lang]に設定する値。
     */
    public void setLang(final String argLang) {
        fLang = argLang;
    }

    /**
     * フィールド [lang] の値を取得します。
     *
     * フィールドの説明: [言語を指定します。必須項目です。]。
     *
     * @return フィールド[lang]から取得した値。
     */
    public String getLang() {
        return fLang;
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
        buf.append("blanco.veevalidate.valueobject.BlancoVeeValidateMessageStructure[");
        buf.append("no=" + fNo);
        buf.append(",message=" + fMessage);
        buf.append(",lang=" + fLang);
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
    public void copyTo(final BlancoVeeValidateMessageStructure target) {
        if (target == null) {
            throw new IllegalArgumentException("Bug: BlancoVeeValidateMessageStructure#copyTo(target): argument 'target' is null");
        }

        // No needs to copy parent class.

        // Name: fNo
        // Type: java.lang.String
        target.fNo = this.fNo;
        // Name: fMessage
        // Type: java.lang.String
        target.fMessage = this.fMessage;
        // Name: fLang
        // Type: java.lang.String
        target.fLang = this.fLang;
    }
}
