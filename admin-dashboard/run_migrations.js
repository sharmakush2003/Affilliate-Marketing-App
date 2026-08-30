const { createClient } = require('@supabase/supabase-js');

const supabaseUrl = "https://pexrjsvpbhfbfxbyzegc.supabase.co";
const serviceRoleKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBleHJqc3ZwYmhmYmZ4Ynl6ZWdjIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc4NjY3ODQ3MywiZXhwIjoyMTAyMjU0NDczfQ.l0ClbQYUOxReZI-bM9r0S2Kg2pZXujpMWLRT2_-iQ04";

const supabase = createClient(supabaseUrl, serviceRoleKey);

async function migrate() {
  console.log('Running 2FA migration via Supabase RPC...');

  // Add otp_code column
  const r1 = await supabase.rpc('exec_sql', {
    sql: 'ALTER TABLE public.admin_sessions ADD COLUMN IF NOT EXISTS otp_code TEXT;'
  });
  console.log('otp_code:', r1.error ? r1.error.message : 'OK');

  // Add otp_expires column  
  const r2 = await supabase.rpc('exec_sql', {
    sql: 'ALTER TABLE public.admin_sessions ADD COLUMN IF NOT EXISTS otp_expires TIMESTAMP WITH TIME ZONE;'
  });
  console.log('otp_expires:', r2.error ? r2.error.message : 'OK');

  // Drop old constraint
  const r3 = await supabase.rpc('exec_sql', {
    sql: "ALTER TABLE public.admin_sessions DROP CONSTRAINT IF EXISTS admin_sessions_status_check;"
  });
  console.log('drop constraint:', r3.error ? r3.error.message : 'OK');

  // Add new constraint
  const r4 = await supabase.rpc('exec_sql', {
    sql: "ALTER TABLE public.admin_sessions ADD CONSTRAINT admin_sessions_status_check CHECK (status IN ('active', 'logged_out', 'pending_2fa'));"
  });
  console.log('add constraint:', r4.error ? r4.error.message : 'OK');

  console.log('Done!');
}

migrate();
