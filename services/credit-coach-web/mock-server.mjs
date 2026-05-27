import express from 'express';
import cors from 'cors';
import { randomUUID } from 'crypto';

const app = express();
app.use(cors());
app.use(express.json());

let consentGranted = true; // Pre-granted for demo

const score = { customerId:"cust-1", provider:"EXPERIAN", score:742, maxScore:1250, band:"good", bandLabel:"Good (700–849)", previousScore:727, change:15, changeDirection:"up", retrievedAt:"2026-05-03T10:00:00Z", isStale:false, dataQualityScore:98 };
const factors = [
  { factorId:"f1", category:"payment_history", impact:"high", direction:"positive", title:"Payment history is strong", description:"No missed payments in 6 years (35% weighting).", weightingPercent:35 },
  { factorId:"f2", category:"credit_age", impact:"medium", direction:"positive", title:"Long credit history", description:"Oldest account: 8 years.", weightingPercent:15 },
  { factorId:"f3", category:"credit_mix", impact:"low", direction:"positive", title:"Good credit mix", description:"Mortgage, credit card, personal loan.", weightingPercent:10 },
  { factorId:"f4", category:"utilisation", impact:"high", direction:"negative", title:"Credit utilisation is 62%", description:"Aim for below 30%.", weightingPercent:30 },
  { factorId:"f5", category:"new_credit", impact:"low", direction:"negative", title:"2 recent applications", description:"2 hard searches in last 6 months.", weightingPercent:10 },
];

// CONSENT
app.get('/mobile/v1/credit-coach/consent', (_, res) => res.json({ data: consentGranted ? [{ consentId:randomUUID(), status:"GRANTED", craProvider:"EXPERIAN", grantedAt:"2026-05-03T09:00:00Z" }] : [] }));
app.get('/mobile/v1/credit-coach/consent-status', (_, res) => res.json({ data: consentGranted ? [{ consentId:"con-1", customerId:"cust-1", status:"GRANTED", craProvider:"EXPERIAN", consentTextVersion:"1.0", grantedAt:"2026-05-03T09:00:00Z", withdrawnAt:null }] : [] }));
app.post('/mobile/v1/credit-coach/consent', (_, res) => { consentGranted=true; res.status(201).json({ consentId:randomUUID(), status:"GRANTED", craProvider:"EXPERIAN", grantedAt:new Date().toISOString() }); });
app.post('/mobile/v1/credit-coach/consents/:id/withdraw', (_, res) => { consentGranted=false; res.json({ data: { consentId:"con-1", status:"WITHDRAWN", withdrawnAt:new Date().toISOString() } }); });

// DASHBOARD (aggregated)
app.get('/mobile/v1/credit-coach/dashboard', (_, res) => res.json({ data: { ...score, topFactors: factors.slice(0,4) }, meta: { source:"cache" } }));

// SCORE
app.get('/mobile/v1/credit-coach/scores/refresh', (_, res) => res.json({ data: score }));
app.post('/mobile/v1/credit-coach/scores/refresh', (_, res) => res.status(202).json({ data: { status:"retrieving", estimatedSeconds:3 } }));

// FACTORS
app.get('/mobile/v1/credit-coach/factors', (_, res) => res.json({ data: { factors, positiveCount:3, negativeCount:2 } }));

// CHANGE EXPLANATION
app.get('/mobile/v1/credit-coach/change-explanation', (_, res) => res.json({ data: { previousScore:727, currentScore:742, totalChange:15, changeDirection:"up", contributors:[{factor:"Credit card balance reduced",pointImpact:12},{factor:"On-time payment",pointImpact:5},{factor:"New application",pointImpact:-2}] } }));

// PLAN (EP-03)
app.get('/mobile/v1/credit-coach/plan', (_, res) => res.json({ data: { customerId:"cust-1", totalPotentialPoints:45, actionsTotal:3, actionsCompleted:1, confidence:"high", updatedAt:"2026-05-03T10:00:00Z", actions:[
  { actionId:"a1", rank:1, title:"Reduce credit card utilisation", description:"Pay down £1,200 to get below 30%", estimatedPointImpact:25, estimatedTimeframe:"2 months", category:"utilisation", status:"in_progress", explanation:"Utilisation above 30% is the biggest factor hurting your score." },
  { actionId:"a2", rank:2, title:"Avoid new credit applications", description:"Wait 6 months for searches to age", estimatedPointImpact:10, estimatedTimeframe:"6 months", category:"new_credit", status:"not_started", explanation:"Each hard search temporarily reduces your score." },
  { actionId:"a3", rank:3, title:"Set up Direct Debit", description:"Ensures on-time payments every month", estimatedPointImpact:10, estimatedTimeframe:"1 month", category:"payment_history", status:"completed", completedAt:"2026-04-28T10:00:00Z", explanation:"Consistent payments protect your score." }
] } }));
app.post('/mobile/v1/credit-coach/plan/refresh', (_, res) => res.status(202).json({ data: { status:"generating", estimatedCompletionSeconds:5 } }));

// MILESTONES
app.get('/mobile/v1/credit-coach/plan/milestones', (_, res) => res.json({ data: { achieved:[{ milestoneId:"m1", type:"score_threshold", title:"Score reached 700!", achievedAt:"2026-02-15T10:00:00Z", scoreAtAchievement:702 }], upcoming:[{ milestoneId:"m2", type:"score_threshold", title:"Reach 750", targetScore:750 }] } }));

// SPENDING IMPACT
app.get('/mobile/v1/credit-coach/plan/spending-impact', (_, res) => res.json({ data: { insights:[
  { category:"Dining out", currentMonthlySpend:450, suggestedReduction:150, affordabilityImpactPercent:12, explanation:"Reducing dining by £150/month improves affordability by 12%" },
  { category:"Subscriptions", currentMonthlySpend:120, suggestedReduction:40, affordabilityImpactPercent:4, explanation:"Reducing subscriptions by £40/month improves affordability by 4%" }
] } }));

// SCORE HISTORY (EP-04)
app.get('/mobile/v1/credit-coach/history', (_, res) => {
  const pts = Array.from({length:12},(_,i)=>({date:`2025-${String(6+i).padStart(2,'0')}-01`,score:698+i*4,band:(698+i*4)>=700?"good":"fair"}));
  res.json({ data: { points:pts, statistics:{current:742,highest:742,highestDate:"2026-05-01",lowest:698,lowestDate:"2025-06-01",average:718,trend:"improving"}, annotations:[{date:"2026-03-15",type:"score_increase",description:"Credit card balance reduced",impact:"+12 points"}] } });
});

// DEBT OVERVIEW
app.get('/mobile/v1/credit-coach/debt', (_, res) => res.json({ data: { totalDebt:28500, totalMonthlyCommitments:850, debtToIncomeRatio:0.34, accounts:[
  { accountType:"credit_card", lender:"Barclays", balance:3200, limit:5000, utilisationPercent:64, monthlyPayment:150, status:"up_to_date", accountAge:"4 years" },
  { accountType:"personal_loan", lender:"Lloyds", balance:12000, limit:null, monthlyPayment:350, status:"up_to_date", accountAge:"1 year 8 months" },
  { accountType:"mortgage", lender:"Nationwide", balance:12000, monthlyPayment:280, status:"up_to_date", accountAge:"5 years" },
  { accountType:"store_card", lender:"HSBC", balance:800, monthlyPayment:35, status:"disputed", accountAge:"2 years" }
], summary:{totalAccounts:4,accountsUpToDate:3,accountsDisputed:1}, disclaimer:"Data from Experian. Contact Experian to dispute." } }));

// SIMULATOR (EP-05)
app.post('/mobile/v1/credit-coach/simulator/run', (req, res) => {
  const { scenarioType, amount } = req.body || {};
  const impacts = { pay_debt:25, close_account:-15, open_credit:-8, miss_payment:-40, reduce_utilisation:20 };
  const impact = impacts[scenarioType] || 10;
  res.json({ data: { currentScore:742, estimatedScore:742+impact, pointImpact:impact, confidence:Math.abs(impact)>20?"high":"medium", scenarioType, disclaimer:"This is an estimate, not a guarantee.", details:{ utilisationBefore:"64%", utilisationAfter: scenarioType==="pay_debt"?"40%":"64%", bandBefore:"Good", bandAfter:742+impact>=850?"Excellent":742+impact>=700?"Good":"Fair", timeframe:"1–2 months" } } });
});

// OFFERS (EP-06)
app.get('/mobile/v1/credit-coach/offers', (_, res) => res.json({ data: { offers:[{ offerId:"off-1", productName:"Lloyds Personal Loan", amount:12000, rate:8.9, apr:8.9, term:60, monthlyPayment:258.43, totalPayable:15505.80, totalChargeForCredit:3505.80, status:"available", validUntil:"2026-06-03T00:00:00Z", representativeExample:"Borrow £12,000 over 60 months at 8.9% p.a. (fixed). Representative 8.9% APR. Monthly: £258.43. Total payable: £15,505.80." }], suppressed:false } }));
app.post('/mobile/v1/credit-coach/offers/:id/accept', (_, res) => res.json({ data: { status:"accepted", reference:"LBG-CC-2026-0542", coolingOffEndDate:"2026-05-20T00:00:00Z", message:"14-day cooling-off period applies." } }));

// SECCI
app.get('/mobile/v1/credit-coach/offers/:id/secci', (_, res) => res.json({ data: { typeOfCredit:"Fixed-sum unsecured personal loan", totalAmountOfCredit:12000, duration:"60 months", rateOfInterest:"8.9% p.a. (fixed)", apr:"8.9% APR", monthlyRepayment:258.43, totalAmountPayable:15505.80, totalChargeForCredit:3505.80, earlyRepaymentFee:"0% (no fee)", latePaymentFee:"£12 per missed payment", rightToWithdraw:"14 calendar days from acceptance. Repay principal + accrued interest within 30 days. No penalty.", rightToEarlyRepayment:"You can repay early at any time with no penalty (CCA s.94)." } }));

// DECISION EXPLANATION
app.get('/mobile/v1/credit-coach/offers/:id/explanation', (_, res) => res.json({ data: { offerId:"off-1", factors:[
  { factor:"Credit score", value:"750 (Very Good)", impact:"positive" },
  { factor:"Affordability check", value:"Passed", impact:"positive" },
  { factor:"Debt-to-income ratio", value:"34%", impact:"neutral" },
  { factor:"Payment history", value:"No missed payments", impact:"positive" },
  { factor:"Risk tier", value:"Tier 2 (Low-Medium)", impact:"neutral" },
  { factor:"Rate offered", value:"8.9% APR", impact:"neutral" }
], rights:{ humanReview:true, expressView:true, contestDecision:true }, regulatoryBasis:"UK GDPR Article 22 — automated decision-making with legal/significant effect" } }));

// ALERTS (EP-07)
app.get('/mobile/v1/credit-coach/alerts', (_, res) => res.json({ data: { alerts:[
  { alertId:"al-1", type:"utilisation_warning", title:"Utilisation approaching 50%", message:"Your Barclays card is at 48%. Consider making a payment.", severity:"medium", status:"unread", createdAt:"2026-05-05T09:15:00Z" },
  { alertId:"al-2", type:"payment_risk", title:"Payment due in 3 days", message:"Your Lloyds loan payment of £350 is due on 8 May.", severity:"high", status:"unread", createdAt:"2026-05-04T14:30:00Z" },
  { alertId:"al-3", type:"product_eligibility", title:"New product available!", message:"Your score improved to 750 — you now qualify for better rates.", severity:"low", status:"read", createdAt:"2026-05-02T10:00:00Z" }
] } }));
app.get('/mobile/v1/credit-coach/alerts/preferences', (_, res) => res.json({ data: { utilisationEnabled:true, utilisationThreshold:50, paymentEnabled:true, eligibilityEnabled:true, scoreChangeEnabled:false, allDisabled:false } }));
app.put('/mobile/v1/credit-coach/alerts/preferences', (_, res) => res.json({ data: { updated:true } }));

// COMPLIANCE (EP-08)
app.get('/mobile/v1/credit-coach/scores/compare', (_, res) => res.json({ data: { bureauScores:[
  { provider:"EXPERIAN", score:942, maxScore:1250, band:"good", normalisedScore:75 },
  { provider:"EQUIFAX", score:724, maxScore:1000, band:"good", normalisedScore:72 },
  { provider:"TRANSUNION", score:512, maxScore:710, band:"good", normalisedScore:72 }
] } }));
app.post('/mobile/v1/credit-coach/data/export', (_, res) => res.json({ data: { status:"processing", estimatedMinutes:2 } }));
app.delete('/mobile/v1/credit-coach/data', (_, res) => res.json({ data: { deleted:true, retained:["consent audit records","CCA-exempt offer records"] } }));

// CHAT
const chatResponses = {
  'score': "Your current Experian credit score is 742 out of 999, which puts you in the 'Good' band. It's gone up 15 points in the last month — mainly because you reduced your credit card balance.",
  'improve': "Here are the top 3 things you can do to improve your score:\n1. Keep your credit utilisation below 30% (you're at 48% on one card)\n2. Continue making all payments on time\n3. Avoid applying for new credit in the next 3 months",
  'factor': "The main factors affecting your score right now are: credit utilisation (negative — 48% on your Barclays card), payment history (positive — 36 months perfect), and account age (positive — average 4.2 years).",
  'default': "That's a great question. Based on your credit profile, I'd suggest focusing on reducing your credit card utilisation — it's the single biggest lever you have right now. Would you like me to show you a simulation of how paying down your balance could affect your score?"
};
app.post('/mobile/v1/credit-coach/chat', (req, res) => {
  const msg = (req.body.message || '').toLowerCase();
  let reply = chatResponses.default;
  if (msg.includes('score') && !msg.includes('improve')) reply = chatResponses.score;
  else if (msg.includes('improve') || msg.includes('better') || msg.includes('increase')) reply = chatResponses.improve;
  else if (msg.includes('factor') || msg.includes('affect') || msg.includes('why')) reply = chatResponses.factor;
  res.json({ data: { reply, sessionId: 'sess-1', timestamp: new Date().toISOString() } });
});

// HEALTH
app.get('/health', (_, res) => res.json({ status:"UP" }));

app.listen(3001, () => console.log('\n🏦 Credit Coach Mock API on http://localhost:3001\n'));
