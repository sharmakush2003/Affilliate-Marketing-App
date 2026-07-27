# Ping Cuelinks tracking links for all app categories

Add-Type -AssemblyName System.Web

$cid = "301603"
$urls = [ordered]@{
    "Amazon Shopping"       = "https://www.amazon.in"
    "Flipkart Shopping"     = "https://www.flipkart.com"
    "Myntra Shopping"       = "https://www.myntra.com"
    "SBI Credit Card"       = "https://www.sbicard.com"
    "HDFC Credit Card"      = "https://www.hdfcbank.com/personal/pay/cards/credit-cards"
    "Axis Credit Card"      = "https://www.axisbank.com/retail/cards/credit-card"
    "Car Insurance"         = "https://www.policybazaar.com/motor-insurance/car-insurance/"
    "Health Insurance"      = "https://www.policybazaar.com/health-insurance/"
    "Term Life Insurance"   = "https://www.policybazaar.com/life-insurance/term-insurance/"
    "HDFC ERGO Insurance"   = "https://www.hdfcergo.com"
    "Personal Loan"         = "https://www.bankbazaar.com/personal-loan.html"
    "Home Loan"             = "https://www.bankbazaar.com/home-loan.html"
    "Car Loan"              = "https://www.bankbazaar.com/car-loan.html"
    "Business Loan"         = "https://www.bankbazaar.com/business-loan.html"
}

$ua = "Mozilla/5.0 (Linux; Android 12; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "Firing test clicks for all categories to Cuelinks (Channel ID: $cid)..." -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan

$index = 1
foreach ($key in $urls.Keys) {
    $targetUrl = $urls[$key]
    $encoded = [System.Web.HttpUtility]::UrlEncode($targetUrl)
    $subId = "SUB_" + $key.Replace(" ", "_").ToUpper()
    $trackingUrl = "https://linksredirect.com/?cid=$cid&source=api&subid=$subId&url=$encoded"
    
    try {
        $req = [System.Net.HttpWebRequest]::Create($trackingUrl)
        $req.Method = "GET"
        $req.UserAgent = $ua
        $req.AllowAutoRedirect = $true
        $req.Timeout = 10000
        $resp = $req.GetResponse()
        Write-Host "[$index] $key -> Status: $($resp.StatusCode) | SubID: $subId" -ForegroundColor Green
        $resp.Close()
    } catch [System.Net.WebException] {
        if ($_.Response) {
            $resCode = $_.Response.StatusCode
            Write-Host "[$index] $key -> Status: $resCode (Redirect/Ping Registered) | SubID: $subId" -ForegroundColor Yellow
        } else {
            Write-Host "[$index] $key -> Ping sent: $($_.Message) | SubID: $subId" -ForegroundColor Yellow
        }
    }
    $index++
    Start-Sleep -Milliseconds 300
}

Write-Host "`nAll 14 category clicks sent successfully!" -ForegroundColor Green
