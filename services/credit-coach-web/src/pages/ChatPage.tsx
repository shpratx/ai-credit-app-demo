import { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { BottomNav } from '../components/BottomNav';

interface Message { id: string; role: 'user' | 'assistant'; content: string; }

export default function ChatPage() {
  const navigate = useNavigate();
  const [messages, setMessages] = useState<Message[]>([
    { id: '1', role: 'user', content: "How's my credit score?" },
    { id: '2', role: 'assistant', content: "<strong>Your score is 742 (Good)</strong><br><br>That's up 15 points from last month! 🎉<br><br>Your biggest strength is your payment history — no missed payments in 6 years.<br><br>The main thing holding you back is credit utilisation at 62%. Reducing this below 30% could boost your score significantly." }
  ]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const bottomRef = useRef<HTMLDivElement>(null);

  useEffect(() => { bottomRef.current?.scrollIntoView({ behavior: 'smooth' }); }, [messages]);

  const quickReplies = ["What's my score?", "Why did it change?", "How to improve?"];

  const send = async (text?: string) => {
    const msg = text || input.trim();
    if (!msg || loading) return;
    setMessages(prev => [...prev, { id: Date.now().toString(), role: 'user', content: msg }]);
    setInput('');
    setLoading(true);
    try {
      const res = await fetch('/mobile/v1/credit-coach/chat', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ message: msg }) });
      const json = await res.json();
      setMessages(prev => [...prev, { id: (Date.now() + 1).toString(), role: 'assistant', content: json.data.reply }]);
    } catch {
      setMessages(prev => [...prev, { id: (Date.now() + 1).toString(), role: 'assistant', content: "Sorry, I couldn't process that. Please try again." }]);
    }
    setLoading(false);
  };

  const s = {
    shell: { maxWidth: 430, margin: '0 auto', height: '100vh', display: 'flex', flexDirection: 'column' as const, background: '#F5F5F5', position: 'relative' as const },
    topBar: { height: 56, background: '#006A4D', display: 'flex', alignItems: 'center', padding: '0 16px', gap: 12 },
    backIcon: { color: '#fff', fontSize: 20, cursor: 'pointer' },
    title: { color: '#fff', fontSize: 17, fontWeight: 700, flex: 1 },
    action: { color: '#fff', fontSize: 20, cursor: 'pointer' },
    chatArea: { flex: 1, overflow: 'auto', padding: 16, display: 'flex', flexDirection: 'column' as const, gap: 12, paddingBottom: 140 },
    msgUser: { alignSelf: 'flex-end' as const, maxWidth: '80%', background: '#006A4D', color: '#fff', padding: '12px 16px', borderRadius: '18px 18px 4px 18px', fontSize: 15, lineHeight: 1.5 },
    msgAssistant: { alignSelf: 'flex-start' as const, maxWidth: '85%', background: '#fff', color: '#1e293b', padding: '12px 16px', borderRadius: '4px 18px 18px 18px', fontSize: 15, lineHeight: 1.5, border: '1px solid #E0E0E0' },
    quickReplies: { display: 'flex', gap: 8, padding: '8px 16px', overflowX: 'auto' as const },
    quickReply: { padding: '8px 14px', borderRadius: 20, border: '1.5px solid #006A4D', background: 'transparent', color: '#006A4D', fontSize: 13, fontWeight: 700, whiteSpace: 'nowrap' as const, cursor: 'pointer' },
    inputBar: { padding: '12px 16px', background: '#fff', borderTop: '1px solid #E0E0E0', display: 'flex', gap: 10, alignItems: 'center' },
    chatInput: { flex: 1, height: 44, padding: '10px 14px', border: '1.5px solid #E0E0E0', borderRadius: 22, fontSize: 15, outline: 'none' },
    sendBtn: { width: 44, height: 44, borderRadius: '50%', background: '#006A4D', border: 'none', color: '#fff', fontSize: 18, cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center' },
  };

  return (
    <div style={s.shell}>
      {/* Top Bar */}
      <div style={s.topBar}>
        <span style={s.backIcon} onClick={() => navigate('/credit-coach/dashboard')}>←</span>
        <span style={s.title}>Credit Coach</span>
        <span style={s.action}>⋮</span>
      </div>

      {/* Chat Messages */}
      <div style={s.chatArea}>
        {messages.map(m => (
          <div key={m.id} style={m.role === 'user' ? s.msgUser : s.msgAssistant} dangerouslySetInnerHTML={{ __html: m.content }} />
        ))}
        {loading && <div style={s.msgAssistant}>Thinking...</div>}
        <div ref={bottomRef} />
      </div>

      {/* Quick Replies */}
      <div style={{ position: 'fixed', bottom: 120, left: '50%', transform: 'translateX(-50%)', width: '100%', maxWidth: 430 }}>
        <div style={s.quickReplies}>
          {quickReplies.map(q => (
            <button key={q} style={s.quickReply} onClick={() => send(q)}>{q}</button>
          ))}
        </div>
        {/* Input Bar */}
        <div style={s.inputBar}>
          <input
            style={s.chatInput}
            value={input}
            onChange={e => setInput(e.target.value)}
            onKeyDown={e => e.key === 'Enter' && send()}
            placeholder="Ask about your credit..."
          />
          <button style={s.sendBtn} onClick={() => send()} disabled={!input.trim() || loading}>→</button>
        </div>
      </div>

      <BottomNav />
    </div>
  );
}
