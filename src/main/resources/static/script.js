var currentLanguage = "en",
  activeService = 0;
var API_URL = "/api";
var authToken = localStorage.getItem("agrilink_token") || "";
var currentUser = JSON.parse(localStorage.getItem("agrilink_user") || "null");
var detectedLocation =
  (currentUser && currentUser.detectedLocation) ||
  localStorage.getItem("agrilink_location") ||
  "";
var locationRequestStarted = false;
var icons = ["🚚", "🛡️", "₹", "🆘", "🗺️", "📅", "🌾", "🤝"];
var names = {
  en: [
    "Shared Transport",
    "Buyer Reliability",
    "True Profit Calculator",
    "Rescue My Harvest",
    "Oversupply Map",
    "Harvest-Time Advisor",
    "Crop Details",
    "Automatic Buyer Matching",
  ],
  te: [
    "రవాణా వాహనాన్ని పంచుకోండి",
    "కొనుగోలుదారు నమ్మకాన్ని చూడండి",
    "నిజమైన లాభాన్ని లెక్కించండి",
    "నా పంటను రక్షించండి",
    "అధిక పంట సరఫరా మ్యాప్",
    "ఉత్తమ కోత సమయ సలహా",
    "పంట వివరాలు",
    "ఆటోమేటిక్ కొనుగోలుదారు మ్యాచింగ్",
  ],
  hi: [
    "साझा परिवहन",
    "खरीदार की विश्वसनीयता",
    "सही लाभ कैलकुलेटर",
    "मेरी फसल बचाएँ",
    "अधिक आपूर्ति नक्शा",
    "सही कटाई समय सलाह",
    "फसल की जानकारी",
    "स्वचालित खरीदार मिलान",
  ],
};
var descriptions = {
  en: [
    "Pool nearby farmers and reduce delivery cost.",
    "Check payment history, cancellations and reviews.",
    "Know real profit after every farming cost.",
    "Find verified buyers after a cancellation.",
    "Check local crop supply before sowing.",
    "Use maturity, weather, prices and demand.",
    "See duration, investment, yield and profit.",
    "Enter crop details and instantly find suitable verified buyers.",
  ],
  te: [
    "సమీప రైతులతో వాహనం పంచుకొని ఖర్చు తగ్గించండి.",
    "చెల్లింపు చరిత్ర, రద్దులు మరియు సమీక్షలను చూడండి.",
    "అన్ని ఖర్చుల తర్వాత అసలు లాభాన్ని తెలుసుకోండి.",
    "రద్దు అయినప్పుడు కొత్త కొనుగోలుదారులను కనుగొనండి.",
    "విత్తే ముందు స్థానిక పంట సరఫరాను చూడండి.",
    "వాతావరణం, ధర మరియు డిమాండ్‌తో కోత సమయం తెలుసుకోండి.",
    "పంట కాలం, పెట్టుబడి, దిగుబడి మరియు లాభాన్ని తెలుసుకోండి.",
    "పంట వివరాలు నమోదు చేసి సరైన కొనుగోలుదారులను కనుగొనండి.",
  ],
  hi: [
    "पास के किसानों के साथ वाहन साझा करके खर्च कम करें।",
    "भुगतान इतिहास, रद्दीकरण और समीक्षाएँ देखें।",
    "सभी खर्चों के बाद असली लाभ जानें।",
    "रद्द होने पर सत्यापित नया खरीदार खोजें।",
    "बुवाई से पहले स्थानीय फसल आपूर्ति देखें।",
    "मौसम, कीमत और मांग से कटाई समय जानें।",
    "अवधि, निवेश, उपज और लाभ देखें।",
    "फसल की जानकारी भरकर सही खरीदार तुरंत खोजें।",
  ],
};
var fields = {
  en: [
    [
      "Crop name",
      "Quantity (kg)",
      "Pickup village",
      "Delivery market",
      "Harvest date",
    ],
    ["Buyer name", "District"],
    [
      "Land size (acres)",
      "Expected yield (number of 100 kg bags)",
      "Selling price per 100 kg bag (₹)",
      "Total farming cost (₹)",
    ],
    [
      "Crop name",
      "Available quantity (kg)",
      "Minimum price per 100 kg bag (₹)",
      "Freshness remaining (days)",
      "Pickup village",
    ],
    ["Planned crop", "Land size (acres)", "District", "Sowing month"],
    ["Crop name", "Sowing date", "Crop stage", "Farm location"],
    ["Crop name", "Land size (acres)", "Season", "Irrigation"],
    [
      "Crop name",
      "Quantity (kg)",
      "Grade",
      "Location",
      "Harvest time remaining (days)",
    ],
  ],
  te: [
    ["పంట పేరు", "పరిమాణం (కిలోలు)", "పికప్ గ్రామం", "మార్కెట్", "కోత తేదీ"],
    ["కొనుగోలుదారు పేరు", "జిల్లా"],
    ["భూమి (ఎకరాలు)", "అంచనా దిగుబడి (100 కిలోల బస్తాల సంఖ్య)", "100 కిలోల బస్తాకు అమ్మకపు ధర (₹)", "మొత్తం వ్యవసాయ ఖర్చు (₹)"],
    [
      "పంట పేరు",
      "అందుబాటులో ఉన్న పరిమాణం (కిలోలు)",
      "100 కిలోల బస్తాకు కనీస ధర (₹)",
      "తాజాదనం మిగిలిన రోజులు",
      "పికప్ గ్రామం",
    ],
    ["వేయాలనుకున్న పంట", "భూమి (ఎకరాలు)", "జిల్లా", "విత్తే నెల"],
    ["పంట పేరు", "విత్తిన తేదీ", "పంట దశ", "పొలం ప్రాంతం"],
    ["పంట పేరు", "భూమి (ఎకరాలు)", "కాలం", "నీటిపారుదల"],
    ["పంట పేరు", "పరిమాణం (కిలోలు)", "గ్రేడ్", "ప్రాంతం", "కోతకు మిగిలిన రోజులు"],
  ],
  hi: [
    ["फसल का नाम", "मात्रा (किलो)", "उठाने का गाँव", "बिक्री बाजार", "कटाई की तारीख"],
    ["खरीदार का नाम", "जिला"],
    ["भूमि (एकड़)", "अनुमानित उपज (100 किलो बोरियों की संख्या)", "प्रति 100 किलो बोरी बिक्री मूल्य (₹)", "कुल खेती लागत (₹)"],
    [
      "फसल का नाम",
      "उपलब्ध मात्रा (किलो)",
      "प्रति 100 किलो बोरी न्यूनतम कीमत (₹)",
      "ताजगी के शेष दिन",
      "उठाने का गाँव",
    ],
    ["योजनाबद्ध फसल", "भूमि (एकड़)", "जिला", "बुवाई का महीना"],
    ["फसल का नाम", "बुवाई की तारीख", "फसल की अवस्था", "खेत का स्थान"],
    ["फसल का नाम", "भूमि (एकड़)", "मौसम", "सिंचाई"],
    ["फसल का नाम", "मात्रा (किलो)", "ग्रेड", "स्थान", "कटाई में शेष दिन"],
  ],
};
var resultLabels = {
  en: [
    ["Nearby farmers", "Shared cost", "You save"],
    ["Reliability score", "On-time payments", "Completed deals"],
    ["Expected revenue", "Total investment", "Net profit"],
    ["Buyers alerted", "Best offer", "Auction time"],
    ["Risk level", "Planned area", "Alternative crop"],
    ["Harvest window", "Maturity", "Buyer demand"],
    ["Duration", "Estimated total investment", "Estimated total profit"],
    ["🥇 Best Match", "🥈 Second Match", "🥉 Third Match"],
  ],
  te: [
    ["సమీప రైతులు", "రవాణా ఖర్చు", "మీ ఆదా"],
    ["నమ్మకపు స్కోరు", "సమయానికి చెల్లింపులు", "పూర్తయిన లావాదేవీలు"],
    ["అంచనా ఆదాయం", "మొత్తం పెట్టుబడి", "నికర లాభం"],
    ["కొనుగోలుదారులు", "ఉత్తమ ధర", "వేలం సమయం"],
    ["ప్రమాద స్థాయి", "ప్రణాళిక విస్తీర్ణం", "ప్రత్యామ్నాయ పంట"],
    ["కోత సమయం", "పంట పక్వత", "డిమాండ్"],
    ["పంట కాలం", "అంచనా మొత్తం పెట్టుబడి", "అంచనా మొత్తం లాభం"],
    ["🥇 ఉత్తమ సరిపోలిక", "🥈 రెండవ సరిపోలిక", "🥉 మూడవ సరిపోలిక"],
  ],
  hi: [
    ["पास के किसान", "परिवहन लागत", "आपकी बचत"],
    ["विश्वसनीयता स्कोर", "समय पर भुगतान", "पूरे सौदे"],
    ["अनुमानित आय", "कुल निवेश", "शुद्ध लाभ"],
    ["खरीदार", "सबसे अच्छा प्रस्ताव", "नीलामी समय"],
    ["जोखिम स्तर", "योजनाबद्ध क्षेत्र", "वैकल्पिक फसल"],
    ["कटाई अवधि", "परिपक्वता", "खरीदार मांग"],
    ["अवधि", "अनुमानित कुल निवेश", "अनुमानित कुल लाभ"],
    ["🥇 सर्वश्रेष्ठ मिलान", "🥈 दूसरा मिलान", "🥉 तीसरा मिलान"],
  ],
};
var results = [
  ["4 within 8 km", "₹1,750", "₹2,250"],
  ["92 / 100", "96%", "148"],
  ["₹86,400", "₹52,000", "₹34,400"],
  ["12 nearby", "₹2,180 / 100 kg bag", "29:45"],
  ["Moderate", "420 acres", "Green gram"],
  ["12–14 November", "94%", "High"],
  ["150–180 days", "₹72,000", "₹94,000 / acre"],
  ["ABC Foods — 96%", "FreshMart — 91%", "XYZ Processing — 87%"],
];
var ui = {
  en: {
    tag: "Smart farming. Fair prices. Better lives.",
    intro: "Every farming decision, made simpler in your language.",
    welcome: "Welcome to AgriLink AI",
    safe: "Sign in using your phone number and password",
    mobile: "Phone number",
    otp: "Sign In",
    guest: "Create Account",
    location: "Location & Language",
    choose: "Choose your language. You can change it later.",
    detected: "DETECTED LOCATION",
    place: "Hyderabad, Telangana",
    suggest: "We detected Telangana. Continue in Telugu?",
    yes: "Yes, continue in Telugu",
    other: "Choose another language",
    locate: "📍 Detect My Location",
    locating: "Detecting location...",
    located: "Location detected and saved",
    locationPermission: "Allow location access in your browser, then try again.",
    locationPrivacy: "Your browser will ask permission. Your coordinates and detected place are saved to your account.",
    hello: "Namaste, Ramesh!",
    question: "What would you like to do today?",
    services: "Everything your farm needs",
    tap: "Tap any card to get started",
    open: "Open service",
    tip: "Today’s farming tip",
    tipText: "Light rain is expected tomorrow. Complete spraying before 3 PM.",
    back: "Back to services",
    enter: "Enter your details",
    show: "Show Result",
    empty: "Your result will appear here",
    complete: "Sample analysis complete",
    yourResult: "Your result",
  },
  te: {
    tag: "తెలివైన వ్యవసాయం. సరైన ధర. మెరుగైన జీవితం.",
    intro: "మీ భాషలో వ్యవసాయ నిర్ణయాలను సులభంగా తీసుకోండి.",
    welcome: "AgriLink AIకి స్వాగతం",
    safe: "మీ ఫోన్ నంబర్ మరియు పాస్‌వర్డ్‌తో సైన్ ఇన్ చేయండి",
    mobile: "మొబైల్ నంబర్",
    otp: "సైన్ ఇన్",
    guest: "ఖాతా సృష్టించండి",
    location: "స్థానం మరియు భాష",
    choose: "మీ భాషను ఎంచుకోండి. తర్వాత కూడా మార్చుకోవచ్చు.",
    detected: "గుర్తించిన స్థానం",
    place: "హైదరాబాద్, తెలంగాణ",
    suggest: "తెలుగులో కొనసాగాలనుకుంటున్నారా?",
    yes: "అవును, తెలుగులో కొనసాగండి",
    other: "మరొక భాషను ఎంచుకోండి",
    locate: "📍 నా స్థానాన్ని గుర్తించండి",
    locating: "స్థానాన్ని గుర్తిస్తున్నాం...",
    located: "స్థానం గుర్తించి సేవ్ చేయబడింది",
    locationPermission: "బ్రౌజర్‌లో స్థాన అనుమతిని ఇచ్చి మళ్లీ ప్రయత్నించండి.",
    locationPrivacy: "మీ బ్రౌజర్ అనుమతి అడుగుతుంది. గుర్తించిన స్థానం మీ ఖాతాలో సేవ్ అవుతుంది.",
    hello: "నమస్కారం, రమేష్!",
    question: "ఈ రోజు మీరు ఏమి చేయాలనుకుంటున్నారు?",
    services: "మీ వ్యవసాయానికి కావాల్సిన అన్ని సేవలు",
    tap: "ప్రారంభించడానికి కార్డును నొక్కండి",
    open: "సేవను తెరవండి",
    tip: "నేటి వ్యవసాయ సూచన",
    tipText:
      "రేపు తేలికపాటి వర్షం పడవచ్చు. ఈ రోజు 3 గంటలలోపు పిచికారీ పూర్తి చేయండి.",
    back: "సేవలకు తిరిగి",
    enter: "మీ వివరాలను నమోదు చేయండి",
    show: "ఫలితం చూపించు",
    empty: "మీ ఫలితం ఇక్కడ కనిపిస్తుంది",
    complete: "నమూనా విశ్లేషణ పూర్తయింది",
    yourResult: "మీ ఫలితం",
  },
  hi: {
    tag: "स्मार्ट खेती। सही दाम। बेहतर जीवन।",
    intro: "खेती के फैसले अपनी भाषा में आसानी से लें।",
    welcome: "AgriLink AI में आपका स्वागत है",
    safe: "फोन नंबर और पासवर्ड से साइन इन करें",
    mobile: "मोबाइल नंबर",
    otp: "साइन इन",
    guest: "खाता बनाएँ",
    location: "स्थान और भाषा",
    choose: "अपनी भाषा चुनें। बाद में भी बदल सकते हैं।",
    detected: "पता लगाया गया स्थान",
    place: "हैदराबाद, तेलंगाना",
    suggest: "क्या तेलुगु में जारी रखना चाहते हैं?",
    yes: "हाँ, तेलुगु में जारी रखें",
    other: "दूसरी भाषा चुनें",
    locate: "📍 मेरा स्थान पता करें",
    locating: "स्थान पता किया जा रहा है...",
    located: "स्थान पता करके सहेजा गया",
    locationPermission: "ब्राउज़र में स्थान की अनुमति दें और फिर कोशिश करें।",
    locationPrivacy: "ब्राउज़र आपसे अनुमति माँगेगा। स्थान आपके खाते में सुरक्षित होगा।",
    hello: "नमस्ते, रमेश!",
    question: "आज आप क्या करना चाहते हैं?",
    services: "आपकी खेती के लिए सभी सेवाएँ",
    tap: "शुरू करने के लिए कार्ड चुनें",
    open: "सेवा खोलें",
    tip: "आज की खेती सलाह",
    tipText: "कल हल्की बारिश हो सकती है। आज 3 बजे से पहले छिड़काव पूरा करें।",
    back: "सेवाओं पर वापस",
    enter: "अपनी जानकारी भरें",
    show: "परिणाम दिखाएँ",
    empty: "आपका परिणाम यहाँ दिखाई देगा",
    complete: "नमूना विश्लेषण पूरा हुआ",
    yourResult: "आपका परिणाम",
  },
};
function byId(id) {
  return document.getElementById(id);
}
function showScreen(id) {
  document.querySelectorAll(".screen").forEach(function (s) {
    s.classList.add("hidden");
  });
  byId(id).classList.remove("hidden");
  if (id === "dashboard") buildCards();
  if (id === "setup") {
    updateLocationDisplay();
    window.setTimeout(function () { requestDeviceLocation(true); }, 250);
  }
}
function text(id, value) {
  if (byId(id)) byId(id).textContent = value;
}
function changeLanguage(language) {
  currentLanguage = language;
  byId("langSelect").value = language;
  var x = ui[language];
  text("tagline", x.tag);
  text("introText", x.intro);
  text("welcome", x.welcome);
  text("safeText", x.safe);
  text("mobileLabel", x.mobile);
  text("otpBtn", x.otp);
  text("guestBtn", x.guest);
  text("locationTitle", x.location);
  text("chooseText", x.choose);
  text("detectedLabel", x.detected);
  text("placeText", detectedLocation || x.place);
  text("suggestText", detectedLocation ? x.located : x.suggest);
  text("yesBtn", x.yes);
  text("otherBtn", x.other);
  text("detectLocationBtn", x.locate);
  text("locationPrivacy", x.locationPrivacy);
  text("hello", x.hello);
  text("question", x.question);
  text("servicesTitle", x.services);
  text("tapText", x.tap);
  text("tipTitle", x.tip);
  text("tipText", x.tipText);
  text("backBtn", "← " + x.back);
  text("enterTitle", x.enter);
  text("resultBtn", x.show);
  text("emptyText", x.empty);
  updateGreeting();
  updateLocationDisplay();
  buildCards();
}
function updateLocationDisplay(message) {
  var x = ui[currentLanguage];
  var place = detectedLocation || (currentUser && currentUser.district) || x.place;
  text("placeText", place);
  text("suggestText", message || (detectedLocation ? x.located : x.suggest));
  text("locationStatusIcon", detectedLocation ? "✓" : "…");
  var chip = byId("dashboardLocation");
  if (chip) chip.textContent = "📍 " + (detectedLocation || x.locate.replace("📍 ", ""));
}
async function requestDeviceLocation(automatic) {
  var x = ui[currentLanguage];
  if (!authToken) return;
  if (!navigator.geolocation) {
    updateLocationDisplay("This device or browser does not support location detection.");
    return;
  }
  if (locationRequestStarted) return;
  locationRequestStarted = true;
  var button = byId("detectLocationBtn"), chip = byId("dashboardLocation");
  if (button) { button.disabled = true; button.textContent = x.locating; }
  if (chip) { chip.disabled = true; chip.textContent = "📍 " + x.locating; }
  updateLocationDisplay(x.locating);
  navigator.geolocation.getCurrentPosition(async function (position) {
    var finalMessage = x.located;
    try {
      var response = await fetch(API_URL + "/location/detect", {
        method: "POST",
        headers: { "Content-Type": "application/json", "Authorization": "Bearer " + authToken },
        body: JSON.stringify({ latitude: position.coords.latitude, longitude: position.coords.longitude })
      });
      var data = await response.json();
      if (!response.ok) throw new Error(data.message || "Location could not be saved");
      detectedLocation = data.location;
      localStorage.setItem("agrilink_location", detectedLocation);
      if (currentUser) {
        currentUser.detectedLocation = data.location;
        currentUser.latitude = data.latitude;
        currentUser.longitude = data.longitude;
        currentUser.district = data.district || currentUser.district;
        localStorage.setItem("agrilink_user", JSON.stringify(currentUser));
      }
    } catch (error) {
      finalMessage = error.message;
    } finally {
      locationRequestStarted = false;
      if (button) { button.disabled = false; button.textContent = x.locate; }
      if (chip) chip.disabled = false;
      updateLocationDisplay(finalMessage);
    }
  }, function (error) {
    locationRequestStarted = false;
    if (button) { button.disabled = false; button.textContent = x.locate; }
    if (chip) chip.disabled = false;
    var message = error.code === 1 ? x.locationPermission : "Location could not be detected. Check GPS and try again.";
    updateLocationDisplay(message);
  }, { enableHighAccuracy: true, timeout: 12000, maximumAge: 300000 });
}
function updateGreeting() {
  if (!currentUser || !currentUser.name) return;
  var prefix = currentLanguage === "te" ? "నమస్కారం" : currentLanguage === "hi" ? "नमस्ते" : "Namaste";
  text("hello", prefix + ", " + currentUser.name + "!");
}
function continueTelugu() {
  changeLanguage("te");
  showScreen("dashboard");
}
function showAuthMode(mode) {
  var signingIn = mode === "signin";
  byId("signInForm").classList.toggle("hidden", !signingIn);
  byId("signUpForm").classList.toggle("hidden", signingIn);
  byId("signInTab").classList.toggle("active", signingIn);
  byId("signUpTab").classList.toggle("active", !signingIn);
  byId("loginMessage").className = "";
  byId("loginMessage").textContent = signingIn ? "Enter your registered phone number and password." : "Create your farmer account. Your password is stored securely.";
}
function cleanMobile(value) { return value.replace(/\D/g, ""); }
function showLoginMessage(message, type) {
  var box = byId("loginMessage"); box.textContent = message; box.className = type || "";
}
async function authenticate(path, payload, destination) {
  showLoginMessage("Please wait...", "");
  var response = await fetch(API_URL + "/auth/" + path, {method:"POST",headers:{"Content-Type":"application/json"},body:JSON.stringify(payload)});
  var data = await response.json();
  if (!response.ok) throw new Error(data.message || "Authentication failed");
  authToken = data.token; currentUser = data.user;
  detectedLocation = currentUser.detectedLocation || "";
  localStorage.setItem("agrilink_token", authToken); localStorage.setItem("agrilink_user", JSON.stringify(currentUser));
  if (detectedLocation) localStorage.setItem("agrilink_location", detectedLocation);
  updateGreeting(); updateLocationDisplay(); showScreen(destination);
}
byId("signInForm").addEventListener("submit", async function (event) {
  event.preventDefault();
  var mobile=cleanMobile(byId("loginMobile").value), password=byId("loginPassword").value;
  if(mobile.length!==10){showLoginMessage("Enter a valid 10 digit phone number.","error");return;}
  try{await authenticate("login",{mobile:mobile,password:password},"dashboard");}catch(error){showLoginMessage(error.message,"error");}
});
byId("signUpForm").addEventListener("submit", async function (event) {
  event.preventDefault();
  var mobile=cleanMobile(byId("signupMobile").value), password=byId("signupPassword").value, confirm=byId("signupConfirmPassword").value;
  if(mobile.length!==10){showLoginMessage("Enter a valid 10 digit phone number.","error");return;}
  if(password.length<6){showLoginMessage("Password must contain at least 6 characters.","error");return;}
  if(password!==confirm){showLoginMessage("Passwords do not match.","error");return;}
  try{await authenticate("register",{name:byId("signupName").value.trim(),mobile:mobile,district:byId("signupDistrict").value.trim(),password:password,language:currentLanguage},"setup");}catch(error){showLoginMessage(error.message,"error");}
});
function logout() {
  authToken="";currentUser=null;detectedLocation="";localStorage.removeItem("agrilink_token");localStorage.removeItem("agrilink_user");localStorage.removeItem("agrilink_location");
  byId("signInForm").reset();byId("loginPassword").value="";showAuthMode("signin");showScreen("login");
}
async function restoreSession() {
  if(!authToken)return;
  try{var response=await fetch(API_URL+"/auth/me",{headers:{Authorization:"Bearer "+authToken}});if(!response.ok)throw new Error();currentUser=await response.json();detectedLocation=currentUser.detectedLocation||"";if(detectedLocation)localStorage.setItem("agrilink_location",detectedLocation);localStorage.setItem("agrilink_user",JSON.stringify(currentUser));updateGreeting();updateLocationDisplay();showScreen("dashboard");}
  catch(error){authToken="";currentUser=null;localStorage.removeItem("agrilink_token");localStorage.removeItem("agrilink_user");}
}
function buildCards() {
  var grid = byId("serviceGrid");
  if (!grid) return;
  var html = "";
  names[currentLanguage].forEach(function (name, i) {
    html +=
      '<button class="card" onclick="openService(' +
      i +
      ')"><span class="icon">' +
      icons[i] +
      "</span><h3>" +
      name +
      "</h3><p>" +
      descriptions[currentLanguage][i] +
      "</p><b>" +
      ui[currentLanguage].open +
      " →</b></button>";
  });
  grid.innerHTML = html;
}
function openService(i) {
  activeService = i;
  showScreen("service");
  text("serviceIcon", icons[i]);
  text("serviceName", names[currentLanguage][i]);
  text("serviceDescription", descriptions[currentLanguage][i]);
  var html = "";
  var place = detectedLocation || (currentUser && currentUser.district) || "";
  var defaults = ["", "", "", "", ""];
  if (i === 0) defaults = ["", "", place, "", ""];
  if (i === 1) defaults = ["", place];
  if (i === 3) defaults = ["", "", "", "", place];
  if (i === 4) defaults = ["", "", place, ""];
  if (i === 5) defaults = ["", "", "", place];
  if (i === 7) defaults = ["Tomato", "2000", "A", place || "Ongole", "2"];
  fields[currentLanguage][i].forEach(function (label, index) {
    var value = defaults[index] || "";
    html +=
      "<label>" +
      label +
      '<input required value="' +
      escapeAttribute(value) +
      '" placeholder="' +
      label +
      '"></label>';
  });
  byId("formFields").innerHTML = html;
  text("enterTitle", ui[currentLanguage].enter);
  text("resultBtn", ui[currentLanguage].show);
  byId("resultBox").innerHTML =
    '<div class="empty">🌱<h3>' + ui[currentLanguage].empty + "</h3></div>";
}
function escapeAttribute(value) {
  return String(value || "").replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
}
byId("serviceForm").addEventListener("submit", async function (e) {
  e.preventDefault();
  var button = byId("resultBtn");
  button.disabled = true;
  button.textContent = "Analyzing...";
  try {
    if (!authToken) throw new Error("Enter your mobile number on the login screen first");
    var values = Array.from(byId("formFields").querySelectorAll("input")).map(function (input) { return input.value; });
    var response = await fetch(API_URL + "/services/" + activeService + "/analyze", {
      method: "POST",
      headers: { "Content-Type": "application/json", "Authorization": "Bearer " + authToken },
      body: JSON.stringify({ values: values, language: currentLanguage })
    });
    var data = await response.json();
    if (!response.ok) throw new Error(data.message || "Analysis failed");
  var html =
    '<div class="analysis"><b class="success">✓ ' +
    ui[currentLanguage].complete +
    "</b><h2>" +
    ui[currentLanguage].yourResult +
    "</h2>";
  resultLabels[currentLanguage][activeService].forEach(function (label, i) {
    html +=
      '<div class="row"><span>' +
      label +
      "</span><b>" +
      (data.results[i] || "—") +
      "</b></div>";
  });
  html += '<p class="note">Saved in database as history record #' + data.recordId + '.</p></div>';
  byId("resultBox").innerHTML = html;
  } catch (error) {
    byId("resultBox").innerHTML = '<div class="analysis"><p class="error">' + error.message + '</p></div>';
  } finally {
    button.disabled = false;
    button.textContent = ui[currentLanguage].show;
  }
});
changeLanguage("en");
restoreSession();
