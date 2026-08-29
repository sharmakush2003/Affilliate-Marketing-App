const apiKey = 'kJoBWVT7Vc05tf9KSJLqMLQaN950o4-b4eQ4MSSTIno';
const baseUrl = 'https://www.cuelinks.com/api/v2';
const headers = { 'Authorization': `Bearer ${apiKey}`, 'Content-Type': 'application/json' };

async function check(endpoint) {
  try {
    const res = await fetch(`${baseUrl}/${endpoint}`, { headers });
    console.log(endpoint, res.status, res.statusText);
    if (res.ok && res.status !== 204) {
      const data = await res.json();
      console.log(endpoint, 'DATA:', JSON.stringify(data).slice(0, 500));
    }
  } catch(e) {
    console.log(endpoint, 'ERR:', e.message);
  }
}

async function run() {
  await check('performance_reports.json');
  await check('event_reports.json');
  await check('link_reports.json');
  await check('channels.json');
  await check('clicks');
  await check('stats');
}
run();
