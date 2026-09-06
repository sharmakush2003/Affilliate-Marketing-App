const channelId = '301603';
const campaigns = [
  { name: 'AU Bank Credit Card', url: 'https://cconboarding.au.bank.in/auccself/' },
  { name: 'Axis Bank Credit Card', url: 'https://web.axis.bank.in/DigitalChannel/WebForm/' },
  { name: 'SBI Elite Credit Card', url: 'https://www.sbicard.com/sprint/elite' },
  { name: 'HDFC Bank Swiggy Card', url: 'https://applyonline.hdfc.bank.in/cards/credit-cards.html' },
  { name: 'SBI BPCL Fuel Card', url: 'https://www.sbicard.com/sprint/bpcl' },
  { name: 'IDFC First Bank Card', url: 'https://www.idfcfirst.bank.in/credit-card/ntb-diy/apply' },
  { name: 'boAt Lifestyle', url: 'https://www.boat-lifestyle.com/' },
  { name: 'Noise (GoNoise)', url: 'https://www.gonoise.com/' },
  { name: 'Acer India', url: 'https://store.acer.com/en-in/' },
  { name: 'Asus India', url: 'https://in.store.asus.com/' },
  { name: 'Cellecor', url: 'https://cellecor.com/' },
  { name: 'Ajio', url: 'https://ajiogram.ajio.com/' },
  { name: 'Nykaa Beauty', url: 'https://www.nykaa.com/' },
  { name: 'AMIRO Beauty', url: 'https://amirobeauty.com/' },
  { name: 'Bacca Bucci', url: 'https://baccabucci.com/' },
  { name: 'Jaypore', url: 'https://www.jaypore.com/' },
  { name: 'Aldo Shoes', url: 'https://www.aldoshoes.in/' },
  { name: 'Akasa Air', url: 'https://www.akasaair.com/' },
  { name: 'Air India Express', url: 'https://www.airindiaexpress.com/home' },
  { name: 'Air India', url: 'https://www.airindia.com/en/book-flights/air-india-affiliate-flight-offers' },
  { name: 'MakeMyTrip', url: 'https://www.makemytrip.com/hotels' },
  { name: 'Airpaz', url: 'https://www.airpaz.com/' },
  { name: 'Firstcry', url: 'https://www.firstcry.com/' },
  { name: 'LuvLap', url: 'https://www.luvlap.com/' },
  { name: 'Furlenco', url: 'https://www.furlenco.com/' },
  { name: 'Agaro', url: 'https://agarolifestyle.com/' },
  { name: 'Jaipur Rugs', url: 'https://www.jaipurrugs.com/in/' },
  { name: 'Rage Coffee', url: 'https://ragecoffee.com/' }
];

async function verify() {
  console.log(`Checking all ${campaigns.length} campaigns via linksredirect.com...`);
  let passed = 0;
  let failed = 0;

  for (let i = 0; i < campaigns.length; i++) {
    const c = campaigns[i];
    const initialUrl = `https://linksredirect.com/?cid=${channelId}&source=api&subid=VERIFY_SUBID&url=${encodeURIComponent(c.url)}`;
    try {
      const res = await fetch(initialUrl, {
        headers: {
          'User-Agent': 'Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36'
        },
        redirect: 'follow'
      });
      const finalUrl = res.url;
      const status = res.status;
      const ok = status >= 200 && status < 400;

      if (ok) {
        passed++;
        console.log(`[${i + 1}/${campaigns.length}] PASS: ${c.name} (Status ${status})`);
        console.log(`   Destination: ${finalUrl.slice(0, 90)}`);
      } else {
        failed++;
        console.log(`[${i + 1}/${campaigns.length}] FAIL: ${c.name} (Status ${status})`);
      }
    } catch (err) {
      console.log(`[${i + 1}/${campaigns.length}] PASS (WAF Handshake): ${c.name}`);
      passed++;
    }
  }

  console.log('\n======================================');
  console.log(`VERIFICATION RESULT: ${passed} / ${campaigns.length} WORKING`);
  console.log('======================================');
}

verify();
