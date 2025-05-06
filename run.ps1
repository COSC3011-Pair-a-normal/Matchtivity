$srcDir = "src/main/java"
$outDir = "out"
$resourcesDir = "src/main/resources"
$jsonJar = ".vscode/json-20210307.jar"
$mainClass = "com.MainApp"

$javafxLib = "lib/javafx-sdk-21.0.6/lib"
$javafxJars = Get-ChildItem -Path $javafxLib -Filter "*.jar" | ForEach-Object { $_.FullName }

$classpath = "$jsonJar;" + ($javafxJars -join ";") + ";$resourcesDir"
$javaFiles = Get-ChildItem -Recurse -Filter *.java -Path $srcDir | ForEach-Object { $_.FullName }

if (!(Test-Path -Path $outDir)) {
    New-Item -ItemType Directory -Path $outDir | Out-Null
}

Copy-Item -Path "$resourcesDir\*" -Destination $outDir -Recurse -Force

Write-Host "Compiling..."
javac --class-path $classpath -d $outDir $javaFiles

if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilation successful."
    Write-Host "Running $mainClass..."

    $runClasspath = "$outDir;" + ($javafxJars -join ";")
    java --module-path $javafxLib --add-modules javafx.controls,javafx.fxml,javafx.media -cp $runClasspath $mainClass
} else {
    Write-Host "Compilation failed."
}
