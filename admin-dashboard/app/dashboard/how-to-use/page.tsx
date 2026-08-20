'use client'

import { HelpCircle, RefreshCcw, Users, ArrowLeftRight, CheckCircle, Database, ShieldAlert, Code, Smartphone } from 'lucide-react'

export default function HowToUsePage() {
  return (
    <div style={{ maxWidth: 840, margin: '0 auto', paddingBottom: 60 }}>
      {/* Header Section */}
      <div style={{ marginBottom: 32, borderBottom: '1px solid #e5e7eb', paddingBottom: 16 }}>
        <h1 style={{ fontSize: 24, fontWeight: 900, color: '#111827', margin: 0, letterSpacing: '-0.03em', display: 'flex', alignItems: 'center', gap: 12 }}>
          <HelpCircle size={26} style={{ color: '#4f46e5' }} /> How To Use (डिटेल्ड एडमिन गाइड)
        </h1>
        <p style={{ fontSize: 13.5, color: '#6b7280', marginTop: 6, lineHeight: 1.5 }}>
          Reward Club App के एडमिन डैशबोर्ड को इस्तेमाल करने, Supabase डेटाबेस को मैनेज करने और CueLinks सिंक प्रोसेस को सेटअप करने की पूरी जानकारी यहाँ दी गई है।
        </p>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: 32 }}>

        {/* 1. Prerequisites Section */}
        <div className="card" style={{ padding: '24px 28px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 16 }}>
            <Database size={20} style={{ color: '#ef4444' }} />
            <h2 style={{ fontSize: 16, fontWeight: 800, color: '#111827', margin: 0 }}>1. Prerequisites & Database Setup (शुरुआती सेटअप)</h2>
          </div>
          <div style={{ paddingLeft: 30 }}>
            <p style={{ color: '#374151', fontSize: 13.5, lineHeight: 1.7, marginBottom: 12 }}>
              डैशबोर्ड पर रियल डेटा देखने और एडमिन राइट्स को इनेबल करने के लिए आपको Supabase में एक SQL क्वेरी चलानी होगी और पर्यावरण वेरिएबल्स को कॉन्फ़िगर करना होगा।
            </p>
            
            <div style={{ background: '#f9fafb', borderRadius: 8, padding: '14px 18px', border: '1px solid #e5e7eb', marginBottom: 16 }}>
              <p style={{ margin: '0 0 6px 0', fontSize: 13, fontWeight: 700, color: '#111827' }}>
                Step A: Supabase में SQL Script रन करें
              </p>
              <p style={{ margin: 0, fontSize: 12.5, color: '#4b5563', lineHeight: 1.6 }}>
                प्रोजेक्ट रूट में दी गई <code style={{ background: '#f3f4f6', padding: '2px 4px', borderRadius: 4, fontFamily: 'monospace' }}>SUPABASE_SCHEMA.sql</code> फ़ाइल के कोड को कॉपी करें। इसे अपने **Supabase SQL Editor** में पेस्ट करके रन करें। इससे आपके डेटाबेस में <code style={{ background: '#f3f4f6', padding: '2px 4px', borderRadius: 4, fontFamily: 'monospace' }}>public.transactions</code> टेबल बन जाएगी जो CueLinks के ऑर्डर्स को स्टोर करेगी।
              </p>
            </div>

            <div style={{ background: '#f9fafb', borderRadius: 8, padding: '14px 18px', border: '1px solid #e5e7eb' }}>
              <p style={{ margin: '0 0 6px 0', fontSize: 13, fontWeight: 700, color: '#111827' }}>
                Step B: Env variables भरें
              </p>
              <p style={{ margin: '0 0 10px 0', fontSize: 12.5, color: '#4b5563', lineHeight: 1.6 }}>
                डैशबोर्ड फ़ोल्डर के अंदर स्थित <code style={{ background: '#f3f4f6', padding: '2px 4px', borderRadius: 4, fontFamily: 'monospace' }}>.env.local</code> फ़ाइल खोलें और निम्न कुंजियाँ सेट करें:
              </p>
              <pre style={{ margin: 0, padding: 12, background: '#111827', color: '#38bdf8', borderRadius: 6, fontSize: 12, fontFamily: 'monospace', overflowX: 'auto' }}>
{`SUPABASE_SERVICE_ROLE_KEY=your_service_role_key
CUELINKS_API_KEY=your_cuelinks_api_key`}
              </pre>
              <p style={{ margin: '8px 0 0 0', fontSize: 11.5, color: '#dc2626', fontWeight: 600, display: 'flex', alignItems: 'center', gap: 4 }}>
                <ShieldAlert size={13} /> ध्यान दें: Service role key का इस्तेमाल केवल सर्वर-साइड फ़ाइलों में ही करें। इसे क्लाइंट फ़ाइलों में लीक न होने दें।
              </p>
            </div>
          </div>
        </div>

        {/* 2. Tracking Flow Section */}
        <div className="card" style={{ padding: '24px 28px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 16 }}>
            <Smartphone size={20} style={{ color: '#2563eb' }} />
            <h2 style={{ fontSize: 16, fontWeight: 800, color: '#111827', margin: 0 }}>2. How Tracking Works (कैशबैक ट्रैक कैसे होता है?)</h2>
          </div>
          <div style={{ paddingLeft: 30 }}>
            <p style={{ color: '#374151', fontSize: 13.5, lineHeight: 1.7, marginBottom: 14 }}>
              यूज़र के कैशबैक को ऑटो-क्रेडिट करने के लिए ऐप और CueLinks के बीच <strong>sub_id</strong> का उपयोग किया जाता है। इसका पूरा फ्लो नीचे समझाया गया है:
            </p>

            <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
              <div style={{ borderLeft: '3px solid #2563eb', paddingLeft: 16 }}>
                <p style={{ margin: 0, fontSize: 13, fontWeight: 700, color: '#111827' }}>A. App Link Generation (यूज़र क्लिक)</p>
                <p style={{ margin: '4px 0 0 0', fontSize: 12.5, color: '#4b5563', lineHeight: 1.6 }}>
                  जब कोई यूज़र ऐप में स्टोर (जैसे Amazon) पर क्लिक करता है, तो हमारा कोट्लिन कोड उसके Supabase User UUID को क्यूलिंक्स रीडायरेक्ट URL में <code style={{ background: '#f3f4f6', padding: '2px 4px', borderRadius: 4, fontFamily: 'monospace' }}>subid</code> पैरामीटर के रूप में जोड़ता है।
                </p>
              </div>

              <div style={{ borderLeft: '3px solid #10b981', paddingLeft: 16 }}>
                <p style={{ margin: 0, fontSize: 13, fontWeight: 700, color: '#111827' }}>B. Purchase Tracking (ऑर्डर रिकॉर्ड)</p>
                <p style={{ margin: '4px 0 0 0', fontSize: 12.5, color: '#4b5563', lineHeight: 1.6 }}>
                  यूज़र जब स्टोर से ख़रीदारी पूरी करता है, तो CueLinks के सर्वर पर वह ऑर्डर यूज़र के <code style={{ background: '#f3f4f6', padding: '2px 4px', borderRadius: 4, fontFamily: 'monospace' }}>subid</code> के साथ सेव हो जाता है।
                </p>
              </div>

              <div style={{ borderLeft: '3px solid #8b5cf6', paddingLeft: 16 }}>
                <p style={{ margin: 0, fontSize: 13, fontWeight: 700, color: '#111827' }}>C. Database Sync & Credit (कैशबैक मिलना)</p>
                <p style={{ margin: '4px 0 0 0', fontSize: 12.5, color: '#4b5563', lineHeight: 1.6 }}>
                  जब आप लाइव सिंक बटन दबाते हैं, तो सर्वर इन ट्रांज़ैक्शन को CueLinks API से फ़ेच करता है, डेटाबेस के <code style={{ background: '#f3f4f6', padding: '2px 4px', borderRadius: 4, fontFamily: 'monospace' }}>profiles</code> टेबल में उस UUID को सर्च करता है, और मैच होने पर आटोमैटिक कॉइन्स यूज़र वॉलेट में जोड़ देता है।
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* 3. Sync Engine Details */}
        <div className="card" style={{ padding: '24px 28px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 16 }}>
            <RefreshCcw size={20} style={{ color: '#10b981' }} />
            <h2 style={{ fontSize: 16, fontWeight: 800, color: '#111827', margin: 0 }}>3. CueLinks Sync & Automation (सिंक कैसे करें?)</h2>
          </div>
          <div style={{ paddingLeft: 30 }}>
            <p style={{ color: '#374151', fontSize: 13.5, lineHeight: 1.7, marginBottom: 14 }}>
               CueLinks ट्रांज़ैक्शंस को डेटाबेस में लोड करने के दो तरीके हैं:
            </p>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16, marginBottom: 16 }}>
              <div style={{ background: '#f9fafb', borderRadius: 8, padding: 16, border: '1px solid #e5e7eb' }}>
                <span style={{ fontSize: 11, fontWeight: 700, color: '#10b981', textTransform: 'uppercase', letterSpacing: '0.04em' }}>मेथड 1</span>
                <p style={{ fontSize: 13, fontWeight: 700, color: '#111827', margin: '4px 0 6px 0' }}>मैन्युअल सिंक (Manual Sync)</p>
                <p style={{ fontSize: 12, color: '#6b7280', margin: 0, lineHeight: 1.5 }}>
                  टॉप हेडर में दिए गए **"Sync CueLinks"** बटन पर क्लिक करें। यह तुरंत API कॉल करेगा, ट्रांज़ैक्शंस को फ़ेच करेगा, और पेज को ऑटो-रिफ्रेश करके डेटा को अपडेट कर देगा।
                </p>
              </div>
              <div style={{ background: '#f9fafb', borderRadius: 8, padding: 16, border: '1px solid #e5e7eb' }}>
                <span style={{ fontSize: 11, fontWeight: 700, color: '#4f46e5', textTransform: 'uppercase', letterSpacing: '0.04em' }}>मेथड 2</span>
                <p style={{ fontSize: 13, fontWeight: 700, color: '#111827', margin: '4px 0 6px 0' }}>ऑटोमैटिक क्रॉन (Cron Payout)</p>
                <p style={{ fontSize: 12, color: '#6b7280', margin: 0, lineHeight: 1.5 }}>
                  अगर आपने इसे Vercel पर डिप्लॉय किया है, तो आप हर 6 घंटे में डेटा को ऑटो-सिंक करने के लिए प्रोजेक्ट रूट में <code style={{ background: '#f3f4f6', padding: '1px 3px', borderRadius: 4 }}>vercel.json</code> कॉन्फ़िगर कर सकते हैं।
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* 4. Manual Operations */}
        <div className="card" style={{ padding: '24px 28px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 16 }}>
            <Users size={20} style={{ color: '#8b5cf6' }} />
            <h2 style={{ fontSize: 16, fontWeight: 800, color: '#111827', margin: 0 }}>4. Users & Transactions Management (यूज़र और ट्रांज़ैक्शन प्रबंधन)</h2>
          </div>
          <div style={{ paddingLeft: 30 }}>
            <p style={{ color: '#374151', fontSize: 13.5, lineHeight: 1.7, marginBottom: 12 }}>
              यूज़र्स के वॉलेट्स और उनके भुगतानों को मैनेज करना बेहद आसान है:
            </p>
            <ul style={{ listStyleType: 'disc', margin: '0 0 16px 20px', padding: 0, fontSize: 13, color: '#4b5563', display: 'flex', flexDirection: 'column', gap: 8 }}>
              <li>
                <strong>यूज़र कॉइन एडजस्टमेंट:</strong> **Users** टैब पर जाएं, किसी भी यूज़र रो (Row) पर क्लिक करें। एक मॉडर्न मॉड्युल खुलेगा जहाँ आप उसके वॉलेट में प्लस/माइनस कर सकते हैं और उसका ऑडिट रीज़न डाल सकते हैं।
              </li>
              <li>
                <strong>भुगतान टॉगल (Paid/Unpaid Status):</strong> **Transactions** टैब में जाकर भुगतान स्टेटस पर क्लिक करें। आप किसी भी कैशबैक को **Paid** या **Unpaid** के रूप में चिह्नित कर सकते हैं ताकि यह रिकॉर्ड साफ़ रहे।
              </li>
            </ul>
          </div>
        </div>

      </div>
    </div>
  )
}
