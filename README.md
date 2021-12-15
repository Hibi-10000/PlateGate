# SpigotPlugin PlateGate

このプラグインはSpigot(Paper)-1.16.5以上で動作します。
動作確認-MC1.16.5のみ

このプラグインは簡単に作成できる感圧板ワープゲートを追加します。

"/pg create ゲート名" でゲートを作成し、
"/pg link ワープ元ゲート名 ワープ先ゲート名" でゲート同士をリンクします。
ワープ元に設定したゲートの感圧板に乗ると、
ワープ先ゲートの作成したときの方向にワープします。
その時ワープした場所2ブロックにブロックがあると、
強制的にそのブロックが削除されます。
他のコマンドは"/pg help"で確認してください。

ゲートの情報はJSONでしか保管できませんが、
MySQLやSQLiteなどに対応しようと考えています。

また、現在プラグインのメッセージが全て日本語ですが,
できるだけ早く英語にできるようにします。
Currently all plugin messages are in Japanese,
I will be able to speak English as soon as possible.

不具合等ありましたら、報告していただけると助かります。

Copyrights © 2021 Hibi_10000 All rights reserved.
