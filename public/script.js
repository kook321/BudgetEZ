let editingTxId = null;
let editingAccId = null;
let accounts = [];
let transactions = [];
let budget = { mode: 'MONTHLY', amount: 0 }; // 🌟 เพิ่มตัวแปรเก็บ Budget
// ================= 1. โหลดข้อมูลและคำนวณเงิน =================
function loadAllData() {
  fetch("http://localhost:8080/api/account", { cache: "no-store" })
    .then(res => res.json())
    .then(accData => {
      accounts = accData;
      return fetch("http://localhost:8080/api/data", { cache: "no-store" });
    })
    .then(res => res.json())
    .then(txData => {
      transactions = txData;
      // 🌟 ตรงนี้คือส่วนที่หายไปครับ! ต้องสั่งให้ดึง Budget ต่อ
      return fetch("http://localhost:8080/api/budget", { cache: "no-store" });
    })
    .then(res => res.json())
    .then(bgData => {
      budget = bgData; // 🌟 เก็บค่า Budget ลงตัวแปรให้หน้าเว็บรู้จัก
      recalculateBalances();
      updateUI(); // คำสั่งนี้จะเรียก updateSummary() ให้เปลี่ยน Text ครับ
    })
    .catch(err => console.error("❌ ติดต่อ Server ไม่ได้: ", err));
}
function recalculateBalances() {
  // 1. รีเซ็ตเงินเริ่มต้นกลับเป็นค่าตั้งต้นก่อน
  accounts.forEach(acc => {
    acc.balance = acc.initialBalance || 0;
  });

  // 2. นำประวัติทั้งหมดมาบวกลบใหม่
  transactions.forEach(tx => {
    if (tx.status === 'COMPLETED') {
      if (tx.type === 'EXPENSE') {
        let acc = accounts.find(a => a.name === tx.from);
        if (acc) acc.balance -= parseFloat(tx.amount);
      } else if (tx.type === 'INCOME') {
        let acc = accounts.find(a => a.name === tx.to);
        if (acc) acc.balance += parseFloat(tx.amount);
      } else if (tx.type === 'TRANSFER') {
        let accFrom = accounts.find(a => a.name === tx.from);
        let accTo = accounts.find(a => a.name === tx.to);
        if (accFrom && accTo) { accFrom.balance -= parseFloat(tx.amount); accTo.balance += parseFloat(tx.amount); }
      }
    }
  });
}

function updateUI() {
  renderAccounts();
  renderTransactions();
  updateSummary();
  updateDropdowns();
}

function updateSummary() {
  let inc = 0, exp = 0, net = 0;
  let todayExp = 0, monthExp = 0;

  const today = new Date().toISOString().split('T')[0];
  const thisMonth = today.substring(0, 7);
  const daysInMonth = new Date(new Date().getFullYear(), new Date().getMonth() + 1, 0).getDate(); // จำนวนวันในเดือนนี้

  accounts.forEach(acc => net += acc.balance);
  transactions.forEach(tx => {
    if (tx.status === 'COMPLETED') {
      if (tx.type === 'INCOME') inc += parseFloat(tx.amount);
      if (tx.type === 'EXPENSE') {
        let amt = parseFloat(tx.amount);
        exp += amt;
        if (tx.date === today) todayExp += amt;
        if (tx.date.substring(0, 7) === thisMonth) monthExp += amt;
      }
    }
  });

  document.getElementById('sumIncome').innerText = inc.toFixed(2);
  document.getElementById('sumExpense').innerText = exp.toFixed(2);
  document.getElementById('netBalance').innerText = net.toFixed(2);

  // 🌟 คำนวณ Budget
  let dailyLimit = 0;
  let monthlyLimit = 0;

  if (budget.amount > 0) {
    if (budget.mode === 'MONTHLY') {
      monthlyLimit = budget.amount;
      dailyLimit = monthlyLimit / daysInMonth;
    } else { // DAILY
      dailyLimit = budget.amount;
      monthlyLimit = dailyLimit * daysInMonth;
    }
  }

  let todayLeft = dailyLimit - todayExp;
  let monthLeft = monthlyLimit - monthExp;

  let todayElem = document.getElementById('todayBudgetLeft');
  let monthElem = document.getElementById('monthlyBudgetLeft');

  todayElem.innerText = budget.amount === 0 ? "ไม่จำกัด" : todayLeft.toFixed(2);
  monthElem.innerText = budget.amount === 0 ? "ไม่จำกัด" : monthLeft.toFixed(2);

  // เปลี่ยนสีถ้าใช้เงินเกินงบ
  todayElem.style.color = todayLeft < 0 ? "#ff4c4c" : "#17a2b8";
  monthElem.style.color = monthLeft < 0 ? "#ff4c4c" : "#17a2b8";
}

// 🌟 ลอจิกสำหรับ Modal: คำนวณให้ดูสดๆ
function previewBudget() {
  let amt = parseFloat(document.getElementById('budgetAmount').value || 0);
  let mode = document.getElementById('budgetMode').value;
  let net = parseFloat(document.getElementById('netBalance').innerText);
  let preview = document.getElementById('budgetPreview');
  let daysInMonth = new Date(new Date().getFullYear(), new Date().getMonth() + 1, 0).getDate();

  if (amt === 0) { preview.innerText = "💡 กรอกจำนวนเงินเพื่อดูการคำนวณ"; return; }

  if (mode === 'MONTHLY') {
    if (amt > net) {
      preview.innerHTML = `<span style="color:#ff4c4c">⚠️ งบรายเดือน (${amt} ฿) เกินยอดเงินคงเหลือที่คุณมี (${net} ฿)</span>`;
    } else {
      let daily = (amt / daysInMonth).toFixed(2);
      preview.innerHTML = `💡 ถ้ายึดตามนี้ คุณจะใช้เงินได้วันละ <b>${daily} บาท</b>`;
    }
  } else {
    let monthNeeded = amt * daysInMonth;
    if (monthNeeded > net) {
      let daysItWillLast = Math.floor(net / amt);
      preview.innerHTML = `<span style="color:#ff4c4c">⚠️ เงินคุณมีไม่พอใช้ทั้งเดือน (เงินจะหมดใน ${daysItWillLast} วัน)</span>`;
    } else {
      let months = (net / monthNeeded).toFixed(1);
      preview.innerHTML = `💡 จากเงินเก็บที่มี คุณจะอยู่รอดได้ประมาณ <b>${months} เดือน</b>`;
    }
  }
}

// 🌟 บันทึกลง Database
function saveBudget() {
  let amt = parseFloat(document.getElementById('budgetAmount').value || 0);
  let mode = document.getElementById('budgetMode').value;
  let net = parseFloat(document.getElementById('netBalance').innerText);
  let daysInMonth = new Date(new Date().getFullYear(), new Date().getMonth() + 1, 0).getDate();

  // ดักจับไม่ให้ตั้งงบเกินเงินที่มี
  if ((mode === 'MONTHLY' && amt > net) || (mode === 'DAILY' && (amt * daysInMonth) > net)) {
    alert("❌ ไม่สามารถบันทึกได้ เพราะคุณตั้งงบเกินยอดเงินคงเหลือที่มีอยู่จริงครับ!");
    return;
  }

  fetch("http://localhost:8080/api/budget", { method: "POST", body: `${mode}|${amt}` })
    .then(() => {
      closeModal('budgetModal');
      loadAllData(); // โหลดข้อมูลวาดหน้าเว็บใหม่
    })
    .catch(err => console.error(err));
}

// ================= 2. ระบบจัดการบัญชี (Account) =================
function editAccount(id) {
  let acc = accounts.find(a => a.id === id);
  if (acc) {
    document.getElementById('newAccName').value = acc.name;
    document.getElementById('newAccBalance').value = acc.initialBalance;
    editingAccId = id;
    document.getElementById('saveAccBtn').innerText = "Update Account";
    document.getElementById('saveAccBtn').style.backgroundColor = "#ff9800";
  }
}

function saveAccount() {
  // 🌟 ใช้ .trim() ตัดช่องว่าง เผื่อเผลอกดสเปซบาร์
  const name = document.getElementById('newAccName').value.trim();
  const bal = parseFloat(document.getElementById('newAccBalance').value || 0);

  if (name) {
    // 🌟 ดักจับเผื่อพิมพ์ชื่อบัญชีซ้ำกับที่มีอยู่แล้ว
    const isDuplicate = accounts.some(acc =>
      acc.name.toLowerCase() === name.toLowerCase() && acc.id !== editingAccId
    );

    if (isDuplicate) {
      alert("❌ ชื่อบัญชี '" + name + "' มีอยู่ในระบบแล้ว กรุณาใช้ชื่ออื่นครับ");
      return;
    }

    let payload = editingAccId ? `${editingAccId}|${name}|${bal}` : `${name}|${bal}`;
    let method = editingAccId ? "PUT" : "POST";

    fetch("http://localhost:8080/api/account", { method: method, body: payload })
      .then(() => {
        editingAccId = null;
        document.getElementById('saveAccBtn').innerText = "Add Account";
        document.getElementById('saveAccBtn').style.backgroundColor = "#28a745";
        document.getElementById('newAccName').value = '';
        document.getElementById('newAccBalance').value = '';

        closeModal('accountModal');
        loadAllData(); // 🌟 โหลดข้อมูลใหม่ (คราวนี้โหลดติดแน่นอนเพราะเราใส่ no-store ไว้ข้างบนแล้ว)
      })
      .catch(err => {
        console.error("Error saving account:", err);
        alert("❌ ไม่สามารถติดต่อ Server ได้");
      });
  } else {
    alert("กรุณากรอกชื่อบัญชีครับ");
  }
}
function removeAccount(id) {
  if (confirm("คุณต้องการลบบัญชีนี้ใช่หรือไม่?")) {
    fetch("http://localhost:8080/api/account", { method: "DELETE", body: id.toString() })
      .then(() => loadAllData());
  }
}

function renderAccounts() {
  const list = document.getElementById('accountsList');
  const manageList = document.getElementById('manageAccList');
  list.innerHTML = ''; manageList.innerHTML = '';
  accounts.forEach(acc => {
    list.innerHTML += `<div class="account-item"><span>${acc.name}</span><span>${acc.balance.toFixed(2)}</span></div>`;
    manageList.innerHTML += `
            <div class="account-item" style="align-items: center;">
                <span>${acc.name}</span> 
                <div>
                    <button style="background:#ff9800; border:none; color:white; border-radius:3px; cursor:pointer; padding: 4px 8px;" onclick="editAccount(${acc.id})">Edit</button>
                    <button style="background:#dc3545; border:none; color:white; border-radius:3px; cursor:pointer; padding: 4px 8px;" onclick="removeAccount(${acc.id})">Del</button>
                </div>
            </div>`;
  });
}

// ================= 3. ระบบจัดการประวัติ (Transaction) =================
function saveTx() {
  const date = document.getElementById('txDate').value;
  const type = document.getElementById('typeSelect').value;
  const name = document.getElementById('txName').value || 'ไม่ระบุชื่อ';
  const category = document.getElementById('categorySelect').value;
  const note = document.getElementById('txNote').value || '-';
  const amt = parseFloat(document.getElementById('amountInput').value || 0);
  const stat = document.getElementById('statusSelect').value;
  const from = document.getElementById('fromAccSelect').value;
  const to = document.getElementById('toAccSelect').value;

  const fromAcc = type === 'INCOME' ? "None" : from;
  const toAcc = type === 'EXPENSE' ? "None" : to;

  let payload = "";
  let fetchMethod = "POST";

  if (editingTxId) {
    payload = `${editingTxId}|${date}|${type}|${name}|${category}|${amt}|${stat}|${fromAcc}|${toAcc}|${note}`;
    fetchMethod = "PUT";
  } else {
    payload = `${date}|${type}|${name}|${category}|${amt}|${stat}|${fromAcc}|${toAcc}|${note}`;
  }

  fetch("http://localhost:8080/api/transaction", {
    method: fetchMethod,
    body: payload
  })
    .then(() => {
      editingTxId = null;
      closeModal('txModal');
      document.getElementById('txName').value = '';
      document.getElementById('amountInput').value = '';
      document.getElementById('txNote').value = '';
      loadAllData(); // เรียกใช้งานโหลดข้อมูลใหม่ที่ถูกต้อง
    })
    .catch(err => console.error("Error saving/updating:", err));
}

function editTx(id) {
  let tx = transactions.find(t => t.id === id);
  if (tx) {
    editingTxId = id;
    document.getElementById('txDate').value = tx.date;
    document.getElementById('typeSelect').value = tx.type;
    toggleTxFields();
    document.getElementById('txName').value = tx.name;
    document.getElementById('categorySelect').value = tx.category;
    document.getElementById('amountInput').value = tx.amount;
    document.getElementById('statusSelect').value = tx.status;
    document.getElementById('txNote').value = tx.note && tx.note !== 'null' ? tx.note : '';
    if (tx.type === 'EXPENSE' || tx.type === 'TRANSFER') document.getElementById('fromAccSelect').value = tx.from;
    if (tx.type === 'INCOME' || tx.type === 'TRANSFER') document.getElementById('toAccSelect').value = tx.to;
    document.getElementById('txModal').style.display = 'flex';
  }
}

function deleteTx(id) {
  if (confirm("คุณต้องการลบรายการนี้ใช่หรือไม่?")) {
    fetch("http://localhost:8080/api/transaction", { method: "DELETE", body: id })
      .then(() => loadAllData())
      .catch(err => console.error(err));
  }
}

function renderTransactions() {
  const tbody = document.getElementById('txTableBody');
  tbody.innerHTML = '';
  const timeFilter = document.getElementById('timeFilter').value;
  const sortFilter = document.getElementById('sortFilter').value;
  const today = new Date().toISOString().split('T')[0];

  let filteredTx = transactions.filter(tx => {
    if (timeFilter === 'ALL') return true;
    if (timeFilter === 'TODAY') return tx.date === today;
    if (timeFilter === 'MONTH') return tx.date.substring(0, 7) === today.substring(0, 7);
    if (timeFilter === 'YEAR') return tx.date.substring(0, 4) === today.substring(0, 4);
    return true;
  });

  filteredTx.sort((a, b) => {
    if (sortFilter === 'DATE_DESC') return new Date(b.date) - new Date(a.date);
    if (sortFilter === 'DATE_ASC') return new Date(a.date) - new Date(b.date);
    if (sortFilter === 'NAME_ASC') return a.name.localeCompare(b.name);
    if (sortFilter === 'TYPE') return a.type.localeCompare(b.type);
    return 0;
  });

  filteredTx.forEach(tx => {
    let statusClass = tx.status === 'COMPLETED' ? 'status-completed' : 'status-pending';
    let noteHtml = tx.note && tx.note !== '-' && tx.note !== 'null' ? `<br><small style="color: #888;">Note: ${tx.note}</small>` : '';
    let fromAcc = (!tx.from || tx.from === 'null') ? '-' : tx.from;
    let toAcc = (!tx.to || tx.to === 'null') ? '-' : tx.to;

    tbody.innerHTML += `
            <tr>
                <td>${tx.date}</td>
                <td><strong>${tx.name}</strong> ${noteHtml}</td>
                <td>${tx.category}</td>
                <td>${tx.type}</td>
                <td>${parseFloat(tx.amount).toFixed(2)}</td>
                <td>${fromAcc}</td>
                <td>${toAcc}</td>
                <td><span class="${statusClass}">${tx.status}</span></td>
                <td>
                    <button style="background:#ff9800; border:none; color:white; border-radius:3px; cursor:pointer; padding: 4px 8px;" onclick="editTx('${tx.id}')">Edit</button>
                    <button style="background:#dc3545; border:none; color:white; border-radius:3px; cursor:pointer; padding: 4px 8px;" onclick="deleteTx('${tx.id}')">Del</button>
                </td>
            </tr>
        `;
  });
}

// ================= 4. UI Helpers =================
function updateDropdowns() {
  const fromSel = document.getElementById('fromAccSelect');
  const toSel = document.getElementById('toAccSelect');
  fromSel.innerHTML = ''; toSel.innerHTML = '';
  accounts.forEach(acc => {
    fromSel.innerHTML += `<option value="${acc.name}">${acc.name}</option>`;
    toSel.innerHTML += `<option value="${acc.name}">${acc.name}</option>`;
  });
}

function toggleTxFields() {
  const type = document.getElementById('typeSelect').value;
  document.getElementById('fromGroup').style.display = (type === 'EXPENSE' || type === 'TRANSFER') ? 'block' : 'none';
  document.getElementById('toGroup').style.display = (type === 'INCOME' || type === 'TRANSFER') ? 'block' : 'none';
  if (type === 'TRANSFER') document.getElementById('categorySelect').value = 'NONE';
}

function openModal(id) {
  document.getElementById(id).style.display = 'flex';
  if (id === 'txModal') document.getElementById('txDate').value = new Date().toISOString().split('T')[0];
}
function closeModal(id) { document.getElementById(id).style.display = 'none'; }

// 🌟 เริ่มต้นทำงานทันทีที่โหลดเว็บเสร็จ
loadAllData();
