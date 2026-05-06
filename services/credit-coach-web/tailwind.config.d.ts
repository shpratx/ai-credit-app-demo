declare const _default: {
    content: string[];
    theme: {
        extend: {
            colors: {
                lloyds: {
                    green: string;
                    'green-dark': string;
                    'green-light': string;
                    'green-mid': string;
                };
                surface: string;
                background: string;
                border: string;
                text: {
                    primary: string;
                    secondary: string;
                    muted: string;
                    error: string;
                };
                status: {
                    positive: string;
                    'positive-bg': string;
                    negative: string;
                    'negative-bg': string;
                    info: string;
                    'info-bg': string;
                    pending: string;
                    'pending-bg': string;
                };
            };
            borderRadius: {
                btn: string;
                card: string;
                hero: string;
            };
            spacing: {
                '13': string;
            };
            fontFamily: {
                sans: [string, string, string, string, string, string];
                bold: [string, string, string, string, string, string];
            };
            fontSize: {
                hero: [string, {
                    lineHeight: string;
                    fontWeight: string;
                }];
                'page-title': [string, {
                    lineHeight: string;
                    fontWeight: string;
                }];
                'card-title': [string, {
                    lineHeight: string;
                    fontWeight: string;
                }];
                body: [string, {
                    lineHeight: string;
                    fontWeight: string;
                }];
                'body-sm': [string, {
                    lineHeight: string;
                    fontWeight: string;
                }];
                caption: [string, {
                    lineHeight: string;
                    fontWeight: string;
                }];
                micro: [string, {
                    lineHeight: string;
                    fontWeight: string;
                }];
            };
            boxShadow: {
                card: string;
                md: string;
            };
        };
    };
    plugins: any[];
};
export default _default;
