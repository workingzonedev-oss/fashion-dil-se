const API_URL = "/api/store";
const UPLOAD_URL = "/api/upload";

const DEFAULT_STORE = {
  updatedAt: "",
  campaigns: [],
  discoverItems: [],
  products: [],
  offers: [],
  notifications: [],
  orders: [],
};

let storeState = structuredClone(DEFAULT_STORE);
let currentOrderFilter = "all";
let productSearchQuery = "";

// ─── DOM refs ───
const campaignForm = document.getElementById("campaign-form");
const discoverForm = document.getElementById("discover-form");
const productForm = document.getElementById("product-form");
const offerForm = document.getElementById("offer-form");
const notificationForm = document.getElementById("notification-form");

const refreshStoreButton = document.getElementById("refresh-store");
const linkStatus = document.getElementById("link-status");
const updatedAt = document.getElementById("updated-at");
const storePreview = document.getElementById("store-preview");
const productSearch = document.getElementById("product-search");

const productCount = document.getElementById("product-count");
const campaignCount = document.getElementById("campaign-count");
const offerCount = document.getElementById("offer-count");
const orderCount = document.getElementById("order-count");
const sideProducts = document.getElementById("side-products");
const sideOrders = document.getElementById("side-orders");

const campaignList = document.getElementById("campaign-list");
const discoverList = document.getElementById("discover-list");
const productList = document.getElementById("product-list");
const offerList = document.getElementById("offer-list");
const notificationList = document.getElementById("notification-list");
const orderList = document.getElementById("order-list");

const clearCampaigns = document.getElementById("clear-campaigns");
const clearDiscoverItems = document.getElementById("clear-discover-items");
const clearProducts = document.getElementById("clear-products");
const clearOffers = document.getElementById("clear-offers");
const clearNotifications = document.getElementById("clear-notifications");
const clearOrders = document.getElementById("clear-orders");

const confirmModal = document.getElementById("confirm-modal");
const confirmTitle = document.getElementById("confirm-title");
const confirmMessage = document.getElementById("confirm-message");
const confirmCancel = document.getElementById("confirm-cancel");
const confirmOk = document.getElementById("confirm-ok");

let pendingConfirmAction = null;

// ─── Utilities ───
function slugify(value) {
  return value.toLowerCase().trim().replace(/[^a-z0-9]+/g, "-").replace(/^-+|-+$/g, "");
}

function formatPrice(value) {
  return new Intl.NumberFormat("en-IN", { style: "currency", currency: "INR", maximumFractionDigits: 0 }).format(value);
}

function formatTimestamp(value) {
  if (!value) return "Not synced yet";
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? value : date.toLocaleString();
}

function showToast(message, type = "success") {
  const container = document.getElementById("toast-container");
  const toast = document.createElement("div");
  toast.className = `toast ${type}`;
  toast.textContent = message;
  container.appendChild(toast);
  setTimeout(() => toast.remove(), 3000);
}

function showConfirm(title, message) {
  return new Promise((resolve) => {
    confirmTitle.textContent = title;
    confirmMessage.textContent = message;
    confirmModal.classList.add("open");
    pendingConfirmAction = resolve;
  });
}

confirmCancel.addEventListener("click", () => {
  confirmModal.classList.remove("open");
  if (pendingConfirmAction) pendingConfirmAction(false);
  pendingConfirmAction = null;
});

confirmOk.addEventListener("click", () => {
  confirmModal.classList.remove("open");
  if (pendingConfirmAction) pendingConfirmAction(true);
  pendingConfirmAction = null;
});

function renderEmpty(message) {
  return `<div class="empty-box"><strong>Nothing here yet</strong><p>${message}</p></div>`;
}

function escapeHtml(text) {
  const div = document.createElement("div");
  div.textContent = text;
  return div.innerHTML;
}

// ─── Image Upload ───
async function uploadImage(file) {
  return new Promise((resolve, reject) => {
    if (file.size > 5 * 1024 * 1024) {
      reject(new Error("File too large (max 5MB)"));
      return;
    }
    const reader = new FileReader();
    reader.onload = async () => {
      try {
        const response = await fetch(UPLOAD_URL, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ image: reader.result }),
        });
        if (!response.ok) throw new Error("Upload failed");
        const data = await response.json();
        resolve(data.url);
      } catch (err) {
        reject(err);
      }
    };
    reader.onerror = () => reject(new Error("Failed to read file"));
    reader.readAsDataURL(file);
  });
}

function setupImageUpload(zone) {
  const fileInput = zone.querySelector(".file-input");
  const placeholder = zone.querySelector(".upload-placeholder");
  const targetId = zone.dataset.target;
  const preview = document.getElementById(targetId);
  const hiddenInput = zone.closest("label")?.querySelector('input[name="imageUrl"]');

  if (!fileInput) return;

  fileInput.addEventListener("change", async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    try {
      placeholder.innerHTML = '<span>Uploading...</span>';
      const url = await uploadImage(file);

      if (hiddenInput) hiddenInput.value = url;

      if (preview) {
        preview.innerHTML = `<img src="${url}" alt="Preview" />`;
        preview.classList.add("active");
      }
      zone.classList.add("has-image");
      placeholder.style.display = "none";
      showToast("Image uploaded successfully");
    } catch (err) {
      placeholder.innerHTML = `
        <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><polyline points="21 15 16 10 5 21"/></svg>
        <span>Click or drag image here</span>
        <small>PNG, JPG, WebP up to 5MB</small>
      `;
      showToast(err.message || "Upload failed", "error");
    }
  });
}

function setupMultiImageUpload(zone) {
  const fileInput = zone.querySelector(".file-input");
  const targetId = zone.dataset.target;
  const previewGrid = document.getElementById(targetId);
  const form = zone.closest("form");
  const hiddenInput = form?.querySelector('input[name="images"]');

  if (!fileInput) return;

  fileInput.addEventListener("change", async (e) => {
    const files = Array.from(e.target.files);
    const currentImages = JSON.parse(hiddenInput?.value || "[]");

    if (currentImages.length + files.length > 6) {
      showToast("Maximum 6 images allowed", "warning");
      return;
    }

    for (const file of files) {
      try {
        const url = await uploadImage(file);
        currentImages.push(url);

        const thumb = document.createElement("div");
        thumb.className = "img-thumb";
        thumb.innerHTML = `
          <img src="${url}" alt="Product" />
          <button type="button" class="remove-img" data-url="${url}">&times;</button>
        `;
        previewGrid.appendChild(thumb);
      } catch (err) {
        showToast(`Failed to upload: ${err.message}`, "error");
      }
    }

    if (hiddenInput) hiddenInput.value = JSON.stringify(currentImages);
    showToast(`${files.length} image(s) uploaded`);
    fileInput.value = "";
  });

  previewGrid.addEventListener("click", (e) => {
    const removeBtn = e.target.closest(".remove-img");
    if (!removeBtn) return;
    const url = removeBtn.dataset.url;
    const currentImages = JSON.parse(hiddenInput?.value || "[]");
    const updated = currentImages.filter((img) => img !== url);
    if (hiddenInput) hiddenInput.value = JSON.stringify(updated);
    removeBtn.closest(".img-thumb").remove();
    showToast("Image removed");
  });
}

// ─── API ───
async function loadStore() {
  try {
    const response = await fetch(API_URL, { cache: "no-store" });
    if (!response.ok) throw new Error(`Failed: ${response.status}`);
    const payload = await response.json();
    storeState = { ...structuredClone(DEFAULT_STORE), ...payload };
    linkStatus.innerHTML = '<span class="status-dot"></span> API Connected';
    renderAll();
  } catch (error) {
    console.error(error);
    linkStatus.innerHTML = '<span class="status-dot" style="background:#c12f2f"></span> API Offline';
  }
}

async function saveStore() {
  const response = await fetch(API_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(storeState),
  });
  if (!response.ok) throw new Error(`Failed to save: ${response.status}`);
  storeState = await response.json();
  renderAll();
}

// ─── Render Stats ───
function renderStats() {
  const publishedProducts = storeState.products.filter((i) => i.status === "Published").length;
  const publishedCampaigns = storeState.campaigns.filter((i) => i.status === "Published").length;
  const publishedOffers = storeState.offers.filter((i) => i.status === "Published").length;
  const totalOrders = storeState.orders.length;

  productCount.textContent = String(publishedProducts);
  campaignCount.textContent = String(publishedCampaigns);
  offerCount.textContent = String(publishedOffers);
  orderCount.textContent = String(totalOrders);
  sideProducts.textContent = String(publishedProducts);
  sideOrders.textContent = String(totalOrders);
  updatedAt.textContent = formatTimestamp(storeState.updatedAt);
  storePreview.textContent = JSON.stringify(storeState, null, 2);
}

// ─── Render helpers ───
function thumbHtml(imageUrl) {
  if (imageUrl) {
    return `<div class="list-thumb"><img src="${escapeHtml(imageUrl)}" alt="" /></div>`;
  }
  return `<div class="list-thumb"><svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><polyline points="21 15 16 10 5 21"/></svg></div>`;
}

function statusBadge(status) {
  return `<div class="meta ${status === "Draft" ? "draft" : ""}">${escapeHtml(status)}</div>`;
}

// ─── Render Campaigns ───
function renderCampaigns() {
  if (!storeState.campaigns.length) {
    campaignList.innerHTML = renderEmpty("Create a campaign and it will appear in the app home slider.");
    return;
  }
  campaignList.innerHTML = storeState.campaigns.map((item) => `
    <article class="list-item">
      <div class="list-item-left">
        ${thumbHtml(item.imageUrl)}
        <div class="list-copy">
          <strong>${escapeHtml(item.title)}</strong>
          <p>${escapeHtml(item.eyebrow)} &middot; ${escapeHtml(item.cta)}</p>
          <small>${escapeHtml(item.subtitle)}</small>
        </div>
      </div>
      <div class="item-actions">
        ${statusBadge(item.status)}
        <button class="edit-btn" type="button" data-edit="campaigns" data-id="${item.id}">Edit</button>
        <button class="danger-btn" type="button" data-remove="campaigns" data-id="${item.id}">Remove</button>
      </div>
    </article>
  `).join("");
}

// ─── Render Products ───
function renderProducts() {
  let items = storeState.products;
  if (productSearchQuery) {
    const q = productSearchQuery.toLowerCase();
    items = items.filter((p) =>
      p.title.toLowerCase().includes(q) ||
      p.categoryPath.toLowerCase().includes(q) ||
      p.collection.toLowerCase().includes(q) ||
      p.subtitle.toLowerCase().includes(q)
    );
  }

  if (!items.length) {
    productList.innerHTML = renderEmpty(productSearchQuery ? "No products match your search." : "Add the first product to populate the app catalog.");
    return;
  }

  productList.innerHTML = items.map((item) => {
    const firstImg = (item.images && item.images.length) ? item.images[0] : item.imageUrl;
    const discount = item.originalPrice > 0 ? Math.round((item.originalPrice - item.price) * 100 / item.originalPrice) : 0;
    return `
      <article class="list-item">
        <div class="list-item-left">
          ${thumbHtml(firstImg)}
          <div class="list-copy">
            <strong>${escapeHtml(item.title)}</strong>
            <p>${escapeHtml(item.categoryPath)} &middot; ${escapeHtml(item.collection)}</p>
            <small>
              <span class="price-tag">${formatPrice(item.price)}</span>
              <span class="price-original">${formatPrice(item.originalPrice)}</span>
              ${discount > 0 ? `<span class="discount-tag">${discount}% off</span>` : ""}
              &middot; ${escapeHtml(item.deliveryLabel)}
              ${item.enableReselling ? `<span class="discount-tag" style="background:rgba(27,94,32,0.12);color:#1B5E20;">Reselling ON &middot; Margin ₹${item.price - (item.resellerPrice || 0)}</span>` : ""}
            </small>
          </div>
        </div>
        <div class="item-actions">
          ${statusBadge(item.status)}
          <button class="edit-btn" type="button" data-edit="products" data-id="${item.id}">Edit</button>
          <button class="danger-btn" type="button" data-remove="products" data-id="${item.id}">Remove</button>
        </div>
      </article>
    `;
  }).join("");
}

// ─── Render Discover Items ───
function formatDiscoverSection(sectionKey) {
  switch (sectionKey) {
    case "trending_collections": return "Trending Collections";
    case "budget_picks": return "Budget Picks";
    case "shop_by_occasion": return "Shop by Mood / Occasion";
    default: return sectionKey;
  }
}

function renderDiscoverItems() {
  if (!storeState.discoverItems.length) {
    discoverList.innerHTML = renderEmpty("Create discover page entries for the app.");
    return;
  }
  discoverList.innerHTML = storeState.discoverItems.map((item) => `
    <article class="list-item">
      <div class="list-item-left">
        ${thumbHtml(item.imageUrl)}
        <div class="list-copy">
          <strong>${escapeHtml(item.title)}</strong>
          <p>${formatDiscoverSection(item.sectionKey)}</p>
          <small>${escapeHtml(item.subtitle)}</small>
        </div>
      </div>
      <div class="item-actions">
        ${statusBadge(item.status)}
        <button class="edit-btn" type="button" data-edit="discoverItems" data-id="${item.id}">Edit</button>
        <button class="danger-btn" type="button" data-remove="discoverItems" data-id="${item.id}">Remove</button>
      </div>
    </article>
  `).join("");
}

// ─── Render Offers ───
function renderOffers() {
  if (!storeState.offers.length) {
    offerList.innerHTML = renderEmpty("Create coupons and offers for the app.");
    return;
  }
  offerList.innerHTML = storeState.offers.map((item) => `
    <article class="list-item">
      <div class="list-item-left">
        <div class="list-thumb"><svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><line x1="19" y1="5" x2="5" y2="19"/><circle cx="6.5" cy="6.5" r="2.5"/><circle cx="17.5" cy="17.5" r="2.5"/></svg></div>
        <div class="list-copy">
          <strong>${escapeHtml(item.code)}</strong>
          <p>${escapeHtml(item.description)}</p>
          <small>Min order: ${formatPrice(item.minimumOrder)}</small>
        </div>
      </div>
      <div class="item-actions">
        ${statusBadge(item.status)}
        <button class="edit-btn" type="button" data-edit="offers" data-id="${item.id}">Edit</button>
        <button class="danger-btn" type="button" data-remove="offers" data-id="${item.id}">Remove</button>
      </div>
    </article>
  `).join("");
}

// ─── Render Notifications ───
function renderNotifications() {
  if (!storeState.notifications.length) {
    notificationList.innerHTML = renderEmpty("Create notifications for the app feed.");
    return;
  }
  notificationList.innerHTML = storeState.notifications.map((item) => `
    <article class="list-item">
      <div class="list-item-left">
        <div class="list-thumb"><svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M18 8A6 6 0 006 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 01-3.46 0"/></svg></div>
        <div class="list-copy">
          <strong>${escapeHtml(item.title)}</strong>
          <p>${escapeHtml(item.message)}</p>
          <small>${escapeHtml(item.type)}</small>
        </div>
      </div>
      <div class="item-actions">
        ${statusBadge(item.status)}
        <button class="edit-btn" type="button" data-edit="notifications" data-id="${item.id}">Edit</button>
        <button class="danger-btn" type="button" data-remove="notifications" data-id="${item.id}">Remove</button>
      </div>
    </article>
  `).join("");
}

// ─── Render Orders ───
function renderOrders() {
  let items = storeState.orders;
  if (currentOrderFilter !== "all") {
    items = items.filter((o) => o.status === currentOrderFilter);
  }

  if (!items.length) {
    orderList.innerHTML = renderEmpty(currentOrderFilter === "all" ? "Orders from the app will appear here." : `No ${currentOrderFilter} orders.`);
    return;
  }

  orderList.innerHTML = items.map((item) => `
    <article class="list-item">
      <div class="list-item-left">
        <div class="list-thumb"><svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/></svg></div>
        <div class="list-copy">
          <strong>${escapeHtml(item.id)}</strong>
          <p>${escapeHtml(item.customerName || "Customer")} &middot; ${escapeHtml(item.paymentMethod || "")} &middot; ${escapeHtml(item.deliveryMethod || "")}</p>
          <small>${escapeHtml(item.placedOn || "")} &middot; ${formatPrice(item.total || 0)}</small>
        </div>
      </div>
      <div class="item-actions">
        <select class="order-status-select" data-order-id="${item.id}">
          <option value="Processing" ${item.status === "Processing" ? "selected" : ""}>Processing</option>
          <option value="Shipped" ${item.status === "Shipped" ? "selected" : ""}>Shipped</option>
          <option value="Delivered" ${item.status === "Delivered" ? "selected" : ""}>Delivered</option>
          <option value="Cancelled" ${item.status === "Cancelled" ? "selected" : ""}>Cancelled</option>
        </select>
        <button class="danger-btn" type="button" data-remove="orders" data-id="${item.id}">Remove</button>
      </div>
    </article>
  `).join("");
}

function renderAll() {
  renderStats();
  renderCampaigns();
  renderDiscoverItems();
  renderProducts();
  renderOffers();
  renderNotifications();
  renderOrders();
}

// ─── Payloads ───
function buildProductPayload(formData) {
  return {
    id: formData.get("editId") || `${slugify(String(formData.get("title")))}-${Date.now()}`,
    title: String(formData.get("title")).trim(),
    subtitle: String(formData.get("subtitle")).trim(),
    collection: String(formData.get("collection")).trim(),
    categoryPath: String(formData.get("categoryPath")).trim(),
    price: Number(formData.get("price")),
    originalPrice: Number(formData.get("originalPrice")),
    rating: Number(formData.get("rating")),
    deliveryLabel: String(formData.get("deliveryLabel")).trim(),
    description: String(formData.get("description")).trim(),
    images: JSON.parse(formData.get("images") || "[]"),
    colors: String(formData.get("colors")).split(",").map((i) => i.trim()).filter(Boolean),
    sizes: String(formData.get("sizes")).split(",").map((i) => i.trim()).filter(Boolean),
    details: [
      ["Fabric / Material", String(formData.get("fabric")).trim()],
      ["Fit", String(formData.get("fit")).trim()],
      ["Style", String(formData.get("style")).trim()],
      ["Pattern", String(formData.get("pattern")).trim()],
      ["Care Instructions", String(formData.get("care")).trim()],
      ["Country of Origin", String(formData.get("origin")).trim()],
    ],
    resellerPrice: Number(formData.get("resellerPrice") || 0),
    enableReselling: formData.get("enableReselling") === "true",
    meeshoSupplierLink: String(formData.get("meeshoSupplierLink") || "").trim(),
    status: String(formData.get("status")),
  };
}

function buildCampaignPayload(formData) {
  return {
    id: formData.get("editId") || `${slugify(String(formData.get("title")))}-${Date.now()}`,
    title: String(formData.get("title")).trim(),
    eyebrow: String(formData.get("eyebrow")).trim(),
    subtitle: String(formData.get("subtitle")).trim(),
    cta: String(formData.get("cta")).trim(),
    imageUrl: String(formData.get("imageUrl") || "").trim(),
    status: String(formData.get("status")),
  };
}

function buildDiscoverPayload(formData) {
  return {
    id: formData.get("editId") || `${slugify(String(formData.get("title")))}-${Date.now()}`,
    sectionKey: String(formData.get("sectionKey")).trim(),
    title: String(formData.get("title")).trim(),
    subtitle: String(formData.get("subtitle")).trim(),
    imageUrl: String(formData.get("imageUrl") || "").trim(),
    status: String(formData.get("status")),
  };
}

function buildOfferPayload(formData) {
  return {
    id: formData.get("editId") || `${slugify(String(formData.get("code")))}-${Date.now()}`,
    code: String(formData.get("code")).trim().toUpperCase(),
    description: String(formData.get("description")).trim(),
    minimumOrder: Number(formData.get("minimumOrder")),
    status: String(formData.get("status")),
  };
}

function buildNotificationPayload(formData) {
  return {
    id: formData.get("editId") || `${slugify(String(formData.get("title")))}-${Date.now()}`,
    title: String(formData.get("title")).trim(),
    message: String(formData.get("message")).trim(),
    type: String(formData.get("type")).trim(),
    status: String(formData.get("status")),
  };
}

// ─── CRUD operations ───
async function upsertItem(collection, payload) {
  const index = storeState[collection].findIndex((i) => i.id === payload.id);
  if (index >= 0) {
    storeState[collection][index] = payload;
    showToast("Item updated successfully");
  } else {
    storeState[collection].unshift(payload);
    showToast("Item added successfully");
  }
  await saveStore();
}

async function removeItem(collection, id) {
  const confirmed = await showConfirm("Remove Item", "Are you sure you want to remove this item? This action cannot be undone.");
  if (!confirmed) return;
  storeState[collection] = storeState[collection].filter((item) => item.id !== id);
  await saveStore();
  showToast("Item removed");
}

async function clearCollection(collection) {
  const confirmed = await showConfirm("Clear All", `Are you sure you want to remove all items from ${collection}? This cannot be undone.`);
  if (!confirmed) return;
  storeState[collection] = [];
  await saveStore();
  showToast(`All ${collection} cleared`);
}

// ─── Edit mode ───
function cancelEdit(form) {
  form.reset();
  form.querySelector('input[name="editId"]').value = "";
  form.querySelectorAll(".cancel-edit-btn").forEach((btn) => (btn.style.display = "none"));
  const submitBtn = form.querySelector(".primary-btn");
  if (submitBtn) submitBtn.textContent = submitBtn.dataset.originalText || submitBtn.textContent;

  // Clear image previews
  form.querySelectorAll(".upload-preview").forEach((p) => {
    p.innerHTML = "";
    p.classList.remove("active");
  });
  form.querySelectorAll(".image-upload-zone").forEach((z) => {
    z.classList.remove("has-image");
    const ph = z.querySelector(".upload-placeholder");
    if (ph) ph.style.display = "";
  });
  form.querySelectorAll('input[name="imageUrl"]').forEach((i) => (i.value = ""));
  form.querySelectorAll('input[name="images"]').forEach((i) => (i.value = "[]"));
  const previewGrid = document.getElementById("product-images-preview");
  if (previewGrid) previewGrid.innerHTML = "";
}

function populateFormForEdit(collection, id) {
  const item = storeState[collection].find((i) => i.id === id);
  if (!item) return;

  let form;
  if (collection === "campaigns") {
    form = campaignForm;
    form.querySelector('[name="title"]').value = item.title || "";
    form.querySelector('[name="eyebrow"]').value = item.eyebrow || "";
    form.querySelector('[name="subtitle"]').value = item.subtitle || "";
    form.querySelector('[name="cta"]').value = item.cta || "";
    form.querySelector('[name="status"]').value = item.status || "Published";
    form.querySelector('[name="imageUrl"]').value = item.imageUrl || "";
    if (item.imageUrl) {
      const preview = document.getElementById("campaign-image-preview");
      preview.innerHTML = `<img src="${item.imageUrl}" alt="Preview" />`;
      preview.classList.add("active");
      preview.closest(".image-upload-zone").classList.add("has-image");
      preview.closest(".image-upload-zone").querySelector(".upload-placeholder").style.display = "none";
    }
  } else if (collection === "discoverItems") {
    form = discoverForm;
    form.querySelector('[name="sectionKey"]').value = item.sectionKey || "trending_collections";
    form.querySelector('[name="title"]').value = item.title || "";
    form.querySelector('[name="subtitle"]').value = item.subtitle || "";
    form.querySelector('[name="status"]').value = item.status || "Published";
    form.querySelector('[name="imageUrl"]').value = item.imageUrl || "";
    if (item.imageUrl) {
      const preview = document.getElementById("discover-image-preview");
      preview.innerHTML = `<img src="${item.imageUrl}" alt="Preview" />`;
      preview.classList.add("active");
      preview.closest(".image-upload-zone").classList.add("has-image");
      preview.closest(".image-upload-zone").querySelector(".upload-placeholder").style.display = "none";
    }
  } else if (collection === "products") {
    form = productForm;
    form.querySelector('[name="title"]').value = item.title || "";
    form.querySelector('[name="subtitle"]').value = item.subtitle || "";
    form.querySelector('[name="collection"]').value = item.collection || "New Arrivals";
    form.querySelector('[name="categoryPath"]').value = item.categoryPath || "";
    form.querySelector('[name="deliveryLabel"]').value = item.deliveryLabel || "Fast Delivery";
    form.querySelector('[name="status"]').value = item.status || "Published";
    form.querySelector('[name="price"]').value = item.price || "";
    form.querySelector('[name="originalPrice"]').value = item.originalPrice || "";
    form.querySelector('[name="rating"]').value = item.rating || "";
    form.querySelector('[name="colors"]').value = (item.colors || []).join(", ");
    form.querySelector('[name="sizes"]').value = (item.sizes || []).join(", ");
    form.querySelector('[name="description"]').value = item.description || "";

    const details = item.details || [];
    const getDetail = (key) => {
      const found = details.find((d) => d[0] === key);
      return found ? found[1] : "";
    };
    form.querySelector('[name="origin"]').value = getDetail("Country of Origin");
    form.querySelector('[name="fabric"]').value = getDetail("Fabric / Material");
    form.querySelector('[name="fit"]').value = getDetail("Fit");
    form.querySelector('[name="style"]').value = getDetail("Style");
    form.querySelector('[name="pattern"]').value = getDetail("Pattern");
    form.querySelector('[name="care"]').value = getDetail("Care Instructions");

    // Reselling
    form.querySelector('[name="enableReselling"]').value = item.enableReselling ? "true" : "false";
    form.querySelector('[name="resellerPrice"]').value = item.resellerPrice || "";
    form.querySelector('[name="meeshoSupplierLink"]').value = item.meeshoSupplierLink || "";

    // Images
    const images = item.images || [];
    form.querySelector('[name="images"]').value = JSON.stringify(images);
    const previewGrid = document.getElementById("product-images-preview");
    previewGrid.innerHTML = images.map((url) => `
      <div class="img-thumb">
        <img src="${url}" alt="Product" />
        <button type="button" class="remove-img" data-url="${url}">&times;</button>
      </div>
    `).join("");
  } else if (collection === "offers") {
    form = offerForm;
    form.querySelector('[name="code"]').value = item.code || "";
    form.querySelector('[name="description"]').value = item.description || "";
    form.querySelector('[name="minimumOrder"]').value = item.minimumOrder || "";
    form.querySelector('[name="status"]').value = item.status || "Published";
  } else if (collection === "notifications") {
    form = notificationForm;
    form.querySelector('[name="title"]').value = item.title || "";
    form.querySelector('[name="message"]').value = item.message || "";
    form.querySelector('[name="type"]').value = item.type || "General";
    form.querySelector('[name="status"]').value = item.status || "Published";
  }

  if (!form) return;

  form.querySelector('input[name="editId"]').value = item.id;
  const cancelBtn = form.querySelector(".cancel-edit-btn");
  if (cancelBtn) cancelBtn.style.display = "";
  const submitBtn = form.querySelector(".primary-btn");
  if (submitBtn) {
    submitBtn.dataset.originalText = submitBtn.dataset.originalText || submitBtn.textContent;
    submitBtn.innerHTML = `
      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
      Update ${collection === "discoverItems" ? "Discover Entry" : collection.slice(0, -1).charAt(0).toUpperCase() + collection.slice(1, -1)}
    `;
  }

  // Scroll to form
  form.scrollIntoView({ behavior: "smooth", block: "start" });
}

// ─── Form Submissions ───
campaignForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  await upsertItem("campaigns", buildCampaignPayload(new FormData(campaignForm)));
  cancelEdit(campaignForm);
});

discoverForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  await upsertItem("discoverItems", buildDiscoverPayload(new FormData(discoverForm)));
  cancelEdit(discoverForm);
});

productForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  await upsertItem("products", buildProductPayload(new FormData(productForm)));
  cancelEdit(productForm);
});

offerForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  await upsertItem("offers", buildOfferPayload(new FormData(offerForm)));
  cancelEdit(offerForm);
});

notificationForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  await upsertItem("notifications", buildNotificationPayload(new FormData(notificationForm)));
  cancelEdit(notificationForm);
});

// ─── Cancel edit buttons ───
document.querySelectorAll(".cancel-edit-btn").forEach((btn) => {
  btn.addEventListener("click", () => cancelEdit(btn.closest("form")));
});

// ─── Refresh ───
refreshStoreButton.addEventListener("click", () => {
  loadStore();
  showToast("Store refreshed");
});

// ─── Clear buttons ───
clearCampaigns.addEventListener("click", () => clearCollection("campaigns"));
clearDiscoverItems.addEventListener("click", () => clearCollection("discoverItems"));
clearProducts.addEventListener("click", () => clearCollection("products"));
clearOffers.addEventListener("click", () => clearCollection("offers"));
clearNotifications.addEventListener("click", () => clearCollection("notifications"));
clearOrders.addEventListener("click", () => clearCollection("orders"));

// ─── Delegated click handlers (remove + edit) ───
document.body.addEventListener("click", (e) => {
  const removeBtn = e.target.closest("[data-remove]");
  if (removeBtn) {
    removeItem(removeBtn.dataset.remove, removeBtn.dataset.id);
    return;
  }

  const editBtn = e.target.closest("[data-edit]");
  if (editBtn) {
    populateFormForEdit(editBtn.dataset.edit, editBtn.dataset.id);
    return;
  }
});

// ─── Order status change ───
document.body.addEventListener("change", async (e) => {
  const select = e.target.closest(".order-status-select");
  if (!select) return;
  const orderId = select.dataset.orderId;
  const newStatus = select.value;
  const order = storeState.orders.find((o) => o.id === orderId);
  if (order) {
    order.status = newStatus;
    await saveStore();
    showToast(`Order status updated to ${newStatus}`);
  }
});

// ─── Order filter tabs ───
document.querySelectorAll(".tab-btn[data-filter]").forEach((btn) => {
  btn.addEventListener("click", () => {
    document.querySelectorAll(".tab-btn[data-filter]").forEach((b) => b.classList.remove("active"));
    btn.classList.add("active");
    currentOrderFilter = btn.dataset.filter;
    renderOrders();
  });
});

// ─── Product search ───
productSearch.addEventListener("input", (e) => {
  productSearchQuery = e.target.value;
  renderProducts();
});

// ─── Nav active state ───
document.querySelectorAll(".nav-item").forEach((link) => {
  link.addEventListener("click", () => {
    document.querySelectorAll(".nav-item").forEach((l) => l.classList.remove("active"));
    link.classList.add("active");
  });
});

// ─── Init image uploads ───
document.querySelectorAll(".image-upload-zone:not(.multi)").forEach(setupImageUpload);
document.querySelectorAll(".image-upload-zone.multi").forEach(setupMultiImageUpload);

// ─── Init ───
loadStore();
window.setInterval(loadStore, 5000);
