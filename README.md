<!--
    This Source Code Form is subject to the terms of the Mozilla Public
    License, v. 2.0. If a copy of the MPL was not distributed with this
    file, You can obtain one at http://mozilla.org/MPL/2.0/.
-->

# PlateGate

<!--
    Inspired by [KonseptGate](https://github.com/DemmyDemon/KonseptGate)
    ([Bukkit Forum](https://bukkit.org/threads/tp-konseptgate-v0-6-2-stone-telepads-1-5-1-r0-2.25907/))
-->
  
簡単に作成できる感圧板ワープゲートを追加します。  

`/pg create <ゲート名>` でゲートを作成し、  
`/pg link <ワープ元ゲート名> <ワープ先ゲート名>` でゲート同士をリンクします。  
ワープ元に設定したゲートの感圧板に乗ると、ワープ先ゲートの作成したときの方向にワープします。  
その時ワープした場所2ブロックにブロックがあると、強制的にそのブロックが削除されます。  
他のコマンドは"/pg help"で確認してください。   

**注**: `foliaSupported`が`true`に設定されていますが、何の対応も動作テストもしていません。  
