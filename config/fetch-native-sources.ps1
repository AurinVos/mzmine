# Fetch exact native sources/build material pinned in the release provenance manifest.
# Run from any directory: powershell -File config/fetch-native-sources.ps1
[CmdletBinding()]
param(
  [string]$OutputDirectory = '',
  [switch]$VerifyOnly
)
$ErrorActionPreference = 'Stop'
$repoRoot = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
$manifestPath = Join-Path $repoRoot 'licenses/native-source-provenance.json'
$manifest = Get-Content -Raw -LiteralPath $manifestPath | ConvertFrom-Json
if ([string]::IsNullOrWhiteSpace($OutputDirectory)) {
  $OutputDirectory = Join-Path $repoRoot $manifest.outputDirectory
}
$outputRoot = [IO.Path]::GetFullPath($OutputDirectory)
if (-not $VerifyOnly) {
  New-Item -ItemType Directory -Path $outputRoot -Force | Out-Null
}
foreach ($artifact in $manifest.artifacts) {
  if ([IO.Path]::GetFileName($artifact.file) -ne $artifact.file) {
    throw "Unsafe artifact filename in manifest: $($artifact.file)"
  }
  $targetPath = [IO.Path]::GetFullPath((Join-Path $outputRoot $artifact.file))
  $valid = (Test-Path -LiteralPath $targetPath) -and
      ((Get-FileHash -LiteralPath $targetPath -Algorithm SHA256).Hash -eq $artifact.sha256)
  if ($valid) {
    Write-Output "Verified $($artifact.file)"
    continue
  }
  if ($VerifyOnly) {
    throw "Missing or incorrect native source artifact: $($artifact.file)"
  }
  $partialPath = $targetPath + '.partial'
  Write-Output "Fetching $($artifact.file)"
  Invoke-WebRequest -Uri $artifact.url -OutFile $partialPath -UseBasicParsing
  $actualHash = (Get-FileHash -LiteralPath $partialPath -Algorithm SHA256).Hash
  if ($actualHash -ne $artifact.sha256) {
    throw "Checksum mismatch for $($artifact.file): expected $($artifact.sha256), got $actualHash"
  }
  Move-Item -LiteralPath $partialPath -Destination $targetPath -Force
  Write-Output "Verified $($artifact.file)"
}
if (-not $VerifyOnly) {
if (-not $VerifyOnly) {
  Copy-Item -LiteralPath $manifestPath -Destination (Join-Path $outputRoot 'native-source-provenance.json') -Force
}
}
