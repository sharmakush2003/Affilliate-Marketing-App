const apiKey = 'kJoBWVT7Vc05tf9KSJLqMLQaN950o4-b4eQ4MSSTIno';
const channelId = '301603';
const baseUrl = 'https://www.cuelinks.com/api/v2';

async function testCueLinksAPI() {
  console.log('====================================================');
  console.log('🔍 FETCHING LIVE DATA FROM CUELINKS API (v2)...');
  console.log('====================================================\n');

  const headers = {
    'Authorization': `Bearer ${apiKey}`,
    'Content-Type': 'application/json'
  };

  // 1. Test Channels API
  try {
    console.log('1️⃣ Fetching CueLinks Channels...');
    const res = await fetch(`${baseUrl}/channels.json`, { headers });
    console.log(`Status: ${res.status} ${res.statusText}`);
    if (res.ok) {
      const data = await res.json();
      console.log('Channels Data:', JSON.stringify(data, null, 2));
    }
  } catch (err) {
    console.error('Error fetching channels:', err.message);
  }

  console.log('\n----------------------------------------------------\n');

  // 2. Test Active Campaigns API
  try {
    console.log('2️⃣ Fetching CueLinks Active Campaigns...');
    const res = await fetch(`${baseUrl}/campaigns.json`, { headers });
    console.log(`Status: ${res.status} ${res.statusText}`);
    if (res.ok) {
      const data = await res.json();
      const campaigns = Array.isArray(data) ? data : (data.campaigns || data.data || []);
      console.log(`Total Campaigns Found: ${campaigns.length}`);
      console.log('Top 5 Active Campaigns Sample:');
      campaigns.slice(0, 5).forEach((c, idx) => {
        console.log(`  [${idx + 1}] ID: ${c.id} | Name: ${c.name || c.campaign_name} | Payout: ${c.payout || c.payout_rate || 'N/A'}`);
      });
    }
  } catch (err) {
    console.error('Error fetching campaigns:', err.message);
  }

  console.log('\n----------------------------------------------------\n');

  // 3. Test Transactions API
  try {
    console.log('3️⃣ Fetching CueLinks Live Transactions...');
    const res = await fetch(`${baseUrl}/transactions.json`, { headers });
    console.log(`Status: ${res.status} ${res.statusText}`);
    if (res.status === 204) {
      console.log('Transactions Result: 204 No Content (0 conversions recorded yet).');
    } else if (res.ok) {
      const data = await res.json();
      console.log('Transactions Data:', JSON.stringify(data, null, 2));
    } else {
      const errText = await res.text();
      console.log('Transactions Error Response:', errText);
    }
  } catch (err) {
    console.error('Error fetching transactions:', err.message);
  }

  console.log('\n====================================================');
  console.log('✅ CUELINKS API VERIFICATION COMPLETE');
  console.log('====================================================');
}

testCueLinksAPI();
