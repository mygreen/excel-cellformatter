======================================
リリースノート
======================================

------------------------
ver.0.5 - 2016-01-16
------------------------

* `#1 <https://github.com/mygreen/excel-cellformatter/issues/1>`_ : プログラム内部での書式の定義をプロパティファイルに定義するようにしました。

  * 現在、英語、日本語のロケールのみサポートですが、今後、他の言語も追加していく予定です。

* `#8 <https://github.com/mygreen/excel-cellformatter/issues/8>`_ : 値がゼロの時の漢数字、大字の変換処理結果が空文字になる不良を修正しました。


------------------------
ver.0.4 - 2015-04-19
------------------------

* `#4 <https://github.com/mygreen/excel-cellformatter/issues/4>`_ : セルの値とセクションが一致しない場合にも値が取得できるようにしました。

  * 今までは、例外、 `NoMatchConditionFormatterException` がスローされていましたが、デフォルトのフォーマッタを利用して処理をするよう改善しました。
  * 例えば、整数を入れて、セルの書式（属性）は文字列形式に設定した場合。

* `#5 <https://github.com/mygreen/excel-cellformatter/issues/5>`_ : セルの値がエラーの場合、Excel上で表示される文字 `#VALUE!` と同様に表示するようにしました。

  * POICellFormatter/JXLCellFormatterのプロパティに、`errorCellAsEmpty` を追加し、値falseの時エラーのときに `#VALUE!` などの値を返します。trueの場合は空文字を返す。
  * ただし、POIの場合は正しくエラー情報が取得できない場合があります。
  
    * POIの場合、xlsの古い形式の場合、全て「#VALUE!」となる。xlsxの形式だと種類ごとに値が取得できる。
    * POIの場合、「#NUM!」の場合、「=DATE(50000,1,1)」でも正常に計算ができる。これは、Javaの日付の表示範囲がExcelよりも広いため。

* `#6 <https://github.com/mygreen/excel-cellformatter/issues/6>`_ : 結合してしている空白のセルの場合StackOverFlowが発生する事象修正。


------------------------
ver.0.3 - 2015-04-11
------------------------

* `#2 <https://github.com/mygreen/excel-cellformatter/issues/2>`_ : フォーマットの戻り値として色などの詳細な情報 `CellFormatResult` を取得できるよにしました。

   * この修正に伴い、既存のメソッド `POICellFormatter#format(...)` 、`JXLCellFormatter#format(...)` の名称を、`#formatAsString(...)` に変更しました。

* `#3 <https://github.com/mygreen/excel-cellformatter/issues/3>`_ : エスケープ文字としてクエスチョン（!）に対応しました。

------------------------
ver.0.2 - 2015-04-04
------------------------

* セクションの判定条件を修正。

  * セクションが2つの場合、1つめのセクションが「ゼロ以上」の条件になるよう修正。
  * セクションが5個以上あるとき、例外 *CustomFormatterParseException* をスローするよう修正。

* Javadocの修正。
* マニュアルの整備。

------------------------
ver.0.1 - 2015-03-22
------------------------

* 初期リリース。

