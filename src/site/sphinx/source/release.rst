======================================
リリースノート
======================================

------------------------
ver.0.12 - 2021-02-22
------------------------

* `#26 <https://github.com/mygreen/excel-cellformatter/issues/26>`_ / `#29 <https://github.com/mygreen/excel-cellformatter/issues/29>`_ : テスト時のライブラリのバージョンを変更。

  * JUnitのバージョンを 4.11 → 4.13.1 に変更。
  * log4jのバージョンを 1.2.14 → 1.2.17 に変更。

* `#27 <https://github.com/mygreen/excel-cellformatter/issues/27>`_ : 前提となるApache POIのバージョンを 3.17 → 4.0以上に変更。
  
  * それに伴い、Javaのバージョンを1.7 → 1.8に変更。

* `#28 <https://github.com/mygreen/excel-cellformatter/issues/28>`_ : テスト用のソースで使用していないパッケージのインポートを整理。


------------------------
ver.0.11 - 2019-04-03
------------------------

* `#24 <https://github.com/mygreen/excel-cellformatter/issues/24>`_ : 新元号「令和」に対応。

------------------------
ver.0.10 - 2018-06-24
------------------------

* 前提となるApache POIのバージョンを、v3.17に変更。
* CellFormatterのインタフェースとして、書式を取得するメソッド ``getPattern()`` を追加。
* 中国語、韓国語に対応。ただし、元号は非対応。
* 漢数字、大字などの変換を行うDBNumXの条件式の内部処理を多国語に対応できるよう変更。

------------------------
ver.0.9.2 - 2017-09-22
------------------------
* `#22 <https://github.com/mygreen/excel-cellformatter/issues/22>`_ : ４つ目のセクションが文字列以外の場合に対応。
* FindBugsによる指摘による、無駄なコードの修正。

------------------------
ver.0.9.1 - 2016-12-22
------------------------
* `#20 <https://github.com/mygreen/excel-cellformatter/issues/20>`_ : ``POICellFormatter`` のテスタにおいて、不明な数式の場合、テストに失敗する事象を修正しました。

------------------------
ver.0.9 - 2016-12-19
------------------------
* `#18 <https://github.com/mygreen/excel-cellformatter/issues/18>`_ : ``ObejctCellFormatter`` に formatterResolverのアクセッサメソッドを追加しました。
* `#19 <https://github.com/mygreen/excel-cellformatter/issues/19>`_ : 元号をカスタマイズできるようにしました。
  詳細は、 :doc:`カスタマイズ方法 <customize>` を参照してください。

------------------------
ver.0.8.3 - 2016-07-02
------------------------
* `#17 <https://github.com/mygreen/excel-cellformatter/issues/17>`_ : 数式を含んだセルの値を評価する際にCellの値を更新しないように修正。

------------------------
ver.0.8.2 - 2016-05-21
------------------------
* `#15 <https://github.com/mygreen/excel-cellformatter/issues/15>`_ : :doc:`標準書式 <format_other_defaultformat>` で、 整数部が10桁未満の小数の場合、精度を動的に変更するよう修正。

* `#16 <https://github.com/mygreen/excel-cellformatter/issues/16>`_ : Excel2000で作成した記号付きの会計書式で、エスケープ処理が正しく行われないケースを修正。

------------------------
ver.0.8.1 - 2016-05-07
------------------------
* ビルドしたバイナリに、関係のないCoverturaのリンクが張られおり、コンパイルエラーとなったためビルドし直しました。
* ``NumberCell`` クラスで、環境によりコンパイルエラーとなる事象を修正しました。

------------------------
ver.0.8 - 2016-04-29
------------------------
* `#13 <https://github.com/mygreen/excel-cellformatter/issues/13>`_ : 会計書式でドイツマルクなどの :doc:`記号付きのロケールの条件式 <format_basic_condition>` (例 ``[$-€407]`` )に対応。

* `#14 <https://github.com/mygreen/excel-cellformatter/issues/14>`_ : :doc:`標準書式 <format_other_defaultformat>` で、 ``100000000000`` 以上または、``0.0000000001`` 以下の数値の場合、指数表示になるよう修正。


------------------------
ver.0.7 - 2016-03-20
------------------------

* `#12 <https://github.com/mygreen/excel-cellformatter/issues/12>`_ : ``POICellFormatter`` において、結合したセルの値を取得するときに、非ブランクセルが左上にない場合に正しく値が取得できない事象を修正。

* ``POICellFormatter`` において、式が設定されているセルの評価に失敗したときに、例外をスローするかどうかのプロパティ ``throwFailEvaluateFormula`` を追加しました。
  
  * 初期値はfalseで、例外はスローされず、エラーセルとして扱われます。
  * 値をtrueにした場合、例外 ``FormulaEvaluateException`` がスローされます。

* ``POICellFormatter`` において、結合されてるセルを考慮するかどうかのプロパティ ``considerMergedCell`` を追加しました。
  
  * POIの場合、結合されている領域は、左上のセル以外はブランクセルとなるため、値が設定してあるセルを走査する必要があるためです。
  * 初期値はtrueで、結合セルを考慮します。

------------------------
ver.0.6 - 2016-02-20
------------------------

* `#10 <https://github.com/mygreen/excel-cellformatter/issues/10>`_ : Javaオブジェクトを直接フォーマットできるクラス :ref:`ObjectCellFormatter <howObjectCellFormatter>`  を追加。

* `#11 <https://github.com/mygreen/excel-cellformatter/issues/11>`_ : ビルドインフォーマット(組み込み書式)の実装方法と英語環境での書式の修正。

  * 組み込み書式の実装方法をプロパティファイル「format.properties」に定義するよう変更。
  * :doc:`英語環境の組み込み書式 <format_other_buiitinformat>` を見直し。 

------------------------
ver.0.5.1 - 2016-01-31
------------------------

* `#9 <https://github.com/mygreen/excel-cellformatter/issues/9>`_ : ``MSLocale`` クラス内の綴り間違いを修正。

  * MSLocale.MSLocale.GERMAY → MSLocale.GERMAN
  * MSLocale.isUnkownById(int) → MSLocale.isKnownById(int)


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

