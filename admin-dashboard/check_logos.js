const urls = [
  // Finance
  { name: 'AU Bank Credit Card', url: 'https://cdn0.cuelinks.com/campaigns/4800/thumb/channels4_profile.jpg?1675231713' },
  { name: 'Axis Bank Credit Card', url: 'https://cdn0.cuelinks.com/campaigns/6727/thumb/Axis_Bank_New_Logo.png?1719213197' },
  { name: 'SBI Elite Credit Card', url: 'https://cdn0.cuelinks.com/campaigns/6692/thumb/sbi_card.png?1718878652' },
  { name: 'HDFC Bank Swiggy Card', url: 'https://cdn0.cuelinks.com/campaigns/6513/thumb/HDFC_bank.png?1714470986' },
  { name: 'SBI BPCL Fuel Card', url: 'https://cdn0.cuelinks.com/campaigns/6692/thumb/sbi_card.png?1718878652' },
  { name: 'IDFC First Bank Card', url: 'https://cdn0.cuelinks.com/campaigns/3639/thumb/IDFC-FIRST-Bank-Logo.png?1590487406' },

  // Electronics
  { name: 'boAt Lifestyle', url: 'https://cdn0.cuelinks.com/campaigns/2144/thumb/boat.png?1582264627' },
  { name: 'Noise (GoNoise)', url: 'https://cdn0.cuelinks.com/campaigns/3430/thumb/noise.png?1580214820' },
  { name: 'Acer India', url: 'https://cdn0.cuelinks.com/campaigns/4360/thumb/acer.png?1635418199' },
  { name: 'Asus India', url: 'https://cdn0.cuelinks.com/campaigns/4488/thumb/asus.png?1641887258' },
  { name: 'Cellecor', url: 'https://cdn0.cuelinks.com/campaigns/5657/thumb/cellecor.png?1712217743' },

  // Fashion
  { name: 'Ajio', url: 'https://cdn0.cuelinks.com/campaigns/66949/thumb/ajio.png?1720078000' },
  { name: 'Nykaa Beauty', url: 'https://cdn0.cuelinks.com/campaigns/1066/thumb/nykaa.png?1582264627' },
  { name: 'AMIRO Beauty', url: 'https://cdn0.cuelinks.com/campaigns/13566/thumb/amiro.png?1683789498' },
  { name: 'Bacca Bucci', url: 'https://cdn0.cuelinks.com/campaigns/6566/thumb/New_Project_-_2024-05-16T111415.561.png?1715838270' },
  { name: 'Jaypore', url: 'https://cdn0.cuelinks.com/campaigns/3081/thumb/jaypore-logo.png?1491988365' },
  { name: 'Aldo Shoes', url: 'https://cdn0.cuelinks.com/campaigns/5184/thumb/logo.png?1680695442' },

  // Travel
  { name: 'Akasa Air', url: 'https://cdn0.cuelinks.com/campaigns/57507/thumb/Screenshot_2026-05-21_153125.jpg?1779357965' },
  { name: 'Air India Express', url: 'https://cdn0.cuelinks.com/campaigns/5195/thumb/logo20250811-2181357-1w4q0tb.jpg?1754903716' },
  { name: 'Air India', url: 'https://cdn0.cuelinks.com/campaigns/5622/thumb/New_Project_-_2024-03-20T124625.923.png?1710919013' },
  { name: 'MakeMyTrip', url: 'https://cdn0.cuelinks.com/campaigns/3801/thumb/logo20250902-1816682-6zb74.jpg?1756809448' },
  { name: 'Airpaz', url: 'https://cdn0.cuelinks.com/campaigns/5281/thumb/New_Project_-_2023-05-22T163516.009.png?1684753539' },

  // Kids & Home
  { name: 'Firstcry', url: 'https://cdn0.cuelinks.com/campaigns/49/thumb/FC-Logo-big_store_tagline.png?1582718900' },
  { name: 'LuvLap', url: 'https://cdn0.cuelinks.com/campaigns/56288/thumb/channels4_profile.jpg?1776774659' },
  { name: 'Furlenco', url: 'https://cdn0.cuelinks.com/campaigns/3705/thumb/Furlenco-logo.png?1540902030' },
  { name: 'Agaro', url: 'https://cdn0.cuelinks.com/campaigns/37498/thumb/New_Project_-_2025-12-22T110320.092.png?1766381618' },
  { name: 'Jaipur Rugs', url: 'https://cdn0.cuelinks.com/campaigns/5555/thumb/New_Project_-_2023-09-27T182120.046.png?1695819098' },
  { name: 'Rage Coffee', url: 'https://cdn0.cuelinks.com/campaigns/4670/thumb/image_%2832%29.png?1649846164' }
];

async function checkLogos() {
  for (const item of urls) {
    try {
      const res = await fetch(item.url, { method: 'HEAD' });
      console.log((res.status === 200 ? 'OK  ' : 'FAIL ' + res.status) + ' : ' + item.name);
    } catch (e) {
      console.log('ERR : ' + item.name + ' -> ' + e.message);
    }
  }
}
checkLogos();
