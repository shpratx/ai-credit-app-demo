export default {
    content: ['./index.html', './src/**/*.{ts,tsx}'],
    theme: {
        extend: {
            colors: {
                lloyds: {
                    green: '#006A4D',
                    'green-dark': '#005238',
                    'green-light': '#E6F2EE',
                    'green-mid': '#00875F',
                },
                surface: '#FFFFFF',
                background: '#F5F5F5',
                border: '#E0E0E0',
                text: {
                    primary: '#1A1A1A',
                    secondary: '#595959',
                    muted: '#888888',
                    error: '#C0392B',
                },
                status: {
                    positive: '#006A4D',
                    'positive-bg': '#E6F2EE',
                    negative: '#C0392B',
                    'negative-bg': '#FDECEC',
                    info: '#1565C0',
                    'info-bg': '#E3F2FD',
                    pending: '#9C5A00',
                    'pending-bg': '#FFF3E0',
                },
            },
            borderRadius: {
                btn: '8px',
                card: '12px',
                hero: '16px',
            },
            spacing: {
                '13': '52px',
            },
            fontFamily: {
                sans: ['GT Ultra Standard', '-apple-system', 'BlinkMacSystemFont', 'Segoe UI', 'Arial', 'sans-serif'],
                bold: ['GT Ultra Medium Bold', '-apple-system', 'BlinkMacSystemFont', 'Segoe UI', 'Arial', 'sans-serif'],
            },
            fontSize: {
                hero: ['48px', { lineHeight: '1.1', fontWeight: '700' }],
                'page-title': ['24px', { lineHeight: '1.25', fontWeight: '700' }],
                'card-title': ['17px', { lineHeight: '1.35', fontWeight: '700' }],
                body: ['16px', { lineHeight: '1.6', fontWeight: '400' }],
                'body-sm': ['14px', { lineHeight: '1.5', fontWeight: '400' }],
                caption: ['13px', { lineHeight: '1.4', fontWeight: '400' }],
                micro: ['12px', { lineHeight: '1.4', fontWeight: '400' }],
            },
            boxShadow: {
                card: '0 1px 4px rgba(0,0,0,0.06)',
                md: '0 2px 8px rgba(0,0,0,0.10)',
            },
        },
    },
    plugins: [],
};
