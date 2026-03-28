JavaPad Pro - Editor de Texto Avançado (Java Swing)
===============================================

Resumo
------
Projeto de exemplo: editor de texto estilo Notepad++ implementado 100% em Java (Swing).

Estrutura
---------
- src/main/java/com/javapad: código fonte
  - core: modelos e controller
  - ui: MainFrame, EditorTab, menus, dialogs
  - persistence: FileManager
  - utils: ConfigManager, ThemeManager, AppLogger
  - plugins: Plugin interface e manager

Como compilar
-------------
Requisitos: JDK 11+.

No Windows (PowerShell):

```powershell
javac -d out --module-source-path src $(Get-ChildItem -Recurse -Filter "*.java" | ForEach-Object { $_.FullName })
java -cp out com.javapad.Main
```

Instruções rápidas
------------------
- Executar `Main` para abrir a janela principal.
- Menu Arquivo: Novo, Abrir, Salvar.
- Editor suporta múltiplas abas, numeração de linhas, undo/redo (Ctrl+Z/Ctrl+Y), buscar/substituir.

Recursos implementados nesta versão
----------------------------------
- Múltiplas abas com indicação de modificação
- Undo/Redo integrado com `UndoManager` e atalhos padrão (Ctrl/Cmd+Z, Ctrl/Cmd+Y)
- Busca/Substituição simples
- Auto-save configurável via `~/.javapad.properties` (chaves: `autosave.enabled`, `autosave.interval.seconds`)
- Tema claro/escuro (toggle) e persistência em `~/.javapad.properties`
- Destaque de sintaxe básico (Java keywords & numbers) no `JTextPane`
- Confirmação ao fechar se houver arquivos não salvos
 - Diálogo de Preferências para configurar autosave, tema e fonte

Arquivo de configuração
-----------------------
O arquivo `~/.javapad.properties` armazena preferências. Exemplos:

```
autosave.enabled=true
autosave.interval.seconds=60
theme=DARK
```

Compilação e execução
----------------------
No Windows (PowerShell):

```powershell
Get-ChildItem -Recurse -Filter "*.java" | ForEach-Object { $_.FullName } | javac -d out @-
java -cp out com.javapad.Main
```

Executando a partir de um IDE (IntelliJ/Eclipse): importe o diretório como projeto Java, a classe principal é `com.javapad.Main`.

Build rápido (scripts)
----------------------
Incluí scripts para facilitar o build e execução no Windows:

- `build.bat` — compila todas as fontes e gera `dist\JavaPadPro.jar`.
- `build.ps1` — versão PowerShell do script de build.
- `run.bat` — executa `dist\JavaPadPro.jar`.

Novas melhorias
 - Sistema de plugins: coloque JARs com implementações de `com.javapad.plugins.Plugin` na pasta `plugins/` na raiz do aplicativo e serão carregados automaticamente no startup. Um `ExamplePlugin` embutido demonstra como um plugin pode adicionar itens de menu.

Empacotando o plugin de exemplo
--------------------------------
Incluí scripts para compilar e empacotar o plugin de exemplo em `plugins/ExamplePlugin.jar`:

Windows (PowerShell):

```
.\build.ps1          # compila o projeto
.\build-plugin.ps1   # compila e empacota ExamplePlugin.jar em ./plugins
```

Windows (CMD):

```
build.bat
build-plugin.bat
```

Depois disso reinicie a aplicação (ou execute `run.bat`) e o plugin na pasta `plugins/` será carregado automaticamente.
- Abertura de arquivos em background (assíncrona) para evitar travamentos da UI.


Uso:

```
# prompt do Windows
build.bat
run.bat

# ou no PowerShell
.\build.ps1
java -jar dist\JavaPadPro.jar
```

Se preferir usar Gradle, rode `gradle build` (ou `./gradlew build` se adicionar o wrapper). O `build.gradle` já está no projeto.

Gradle Wrapper
--------------
Incluí scripts `gradlew` e `gradlew.bat` e um `gradle/wrapper/gradle-wrapper.properties` apontando para uma distribuição Gradle.
Para completar o Wrapper (baixar o JAR do wrapper) execute no seu ambiente com Gradle instalado:

```
gradle wrapper
```

Depois disso você poderá usar:

```
./gradlew build    # Unix / PowerShell WSL
gradlew.bat build  # Windows
```

Se preferir não instalar o Gradle, use os scripts `build.bat`/`build.ps1` já incluídos para compilar com `javac` e gerar `dist\JavaPadPro.jar`.


Melhorias futuras sugeridas
---------------------------
- Sintaxe highlighting mais robusto (lexers) e temas de cores.
- Sistema de plugins carregáveis dinamicamente (JARs).
- Preferências GUI para atalhos e fontes.
- Melhor tratamento de arquivos grandes (mapeamento de arquivo, streaming).
