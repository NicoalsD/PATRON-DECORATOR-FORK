"use strict";
const form = document.querySelector('#quote-form');
const selected = ['cold', 'monitor'];
const serviceCatalog = new Map([...document.querySelectorAll('.service-option')].map(option => {
  const input = option.querySelector('input');
  return [input.value, {name:option.querySelector('.service-title').textContent, description:option.querySelector('.service-desc').textContent,className:option.dataset.className}];
}));
const classNames = Object.fromEntries([...serviceCatalog].map(([id,service])=>[id,service.className]));
const money = value => new Intl.NumberFormat('es-CO', {style:'currency',currency:'COP',maximumFractionDigits:0}).format(value);
let latest = null, dirty = true, busy = false;
const $ = id => document.getElementById(id);
function node(tag, text, className) { const e=document.createElement(tag); if(text!==undefined)e.textContent=text; if(className)e.className=className; return e; }
function arrow(up) { const svg=document.createElementNS('http://www.w3.org/2000/svg','svg');svg.setAttribute('viewBox','0 0 20 20');svg.setAttribute('aria-hidden','true');const p=document.createElementNS(svg.namespaceURI,'path');p.setAttribute('d',up?'M10 16V4m-5 5 5-5 5 5':'M10 4v12m-5-5 5 5 5-5');svg.append(p);return svg; }
function expression() {
  let result='new StandardShipment(context)';
  for(const id of selected) result=`new ${classNames[id]}(${result}${id==='cold'?', profile.createPackaging(), context.weightKg()':id==='monitor'?', profile.createSensor()':id==='insurance'?', context.declaredValue()':''})`;
  return result;
}
function renderLayers() {
  $('route-origin').textContent=$('origin').value;$('route-destination').textContent=$('destination').value;
  $('layer-count').textContent=`${selected.length} ${selected.length===1?'decorador':'decoradores'}`;
  const list=$('layer-list');list.replaceChildren();
  if(!selected.length)list.append(node('li','Envío base: sin servicios añadidos. Su contrato sigue siendo Shipment.','empty-layers'));
  selected.forEach((id,index) => {
    const item=node('li',undefined,'layer');item.id=`layer-${id}`;item.tabIndex=-1;const top=node('div',undefined,'layer-top');top.append(node('strong',serviceCatalog.get(id).name));
    const controls=node('div',undefined,'order-buttons');
    [true,false].forEach(up=>{const button=node('button');button.type='button';button.setAttribute('aria-label',`${up?'Mover antes':'Mover después'}: ${serviceCatalog.get(id).name}`);button.title=button.getAttribute('aria-label');button.disabled=busy||(up?index===0:index===selected.length-1);button.append(arrow(up));button.addEventListener('click',()=>{const target=index+(up?-1:1);[selected[index],selected[target]]=[selected[target],selected[index]];changed();$(`layer-${id}`).focus();});controls.append(button);});
    top.append(controls);item.append(top,node('code',classNames[id]));const bottom=node('div',undefined,'layer-bottom');bottom.append(node('span',index===selected.length-1?'CAPA EXTERIOR':`CAPA ${index+1}`,'layer-position'));
    const line=latest?.decorated.lines.find(line=>line.id===id);bottom.append(node('span',!dirty&&line?money(line.amount):'por calcular','layer-price'));item.append(bottom);list.append(item);
  });
  $('java-expression').textContent=(!dirty&&latest?latest.decorated.expression:expression()).replaceAll('(new', '(' + String.fromCharCode(10) + '  new');
}
function applyProfileRates() {
  const profile=$('profile').selectedOptions[0];if(!profile)return;
  const rates={cold:profile.dataset.packagingRate,monitor:profile.dataset.sensorRate};
  Object.entries(rates).forEach(([id,rate])=>{const option=form.querySelector(`input[name=services][value=${id}]`)?.closest('.service-option');if(option&&rate)option.querySelector('.service-rate').textContent=rate;});
}
function changed() {
  $('compare-result').hidden=true;
  dirty=true;$('download').disabled=true;$('print').disabled=true;$('quote-status').textContent=latest?'Cambios pendientes · vuelve a calcular':'Listo para calcular';$('quote-status').classList.add('dirty');$('form-error').hidden=true;renderLayers();
}
function showResult(data) {
  const quote=data.decorated;const host=$('quote-result');host.replaceChildren(node('div',money(quote.total),'price-total'),node('div','TOTAL DEL ENVÍO · COP','currency'));
  const lines=node('div',undefined,'quote-lines');quote.lines.forEach(line=>{const row=node('div',undefined,'quote-line');row.append(node('span',line.name),node('span',money(line.amount)));lines.append(row);});host.append(lines);
  const delivery=node('div',undefined,'delivery');delivery.append(node('span','Plazo estimado del ejemplo'),node('strong',`${quote.deliveryHours} h`));host.append(delivery);
  host.append(node('p',`Base: ${money(data.base.total)} · Servicios añadidos: ${money(quote.total-data.base.total)}`,'delta'));
  const capabilities=node('ul',undefined,'capabilities');quote.capabilities.forEach(c=>{const item=node('li');const mark=document.createElementNS('http://www.w3.org/2000/svg','svg');mark.setAttribute('viewBox','0 0 20 20');mark.setAttribute('aria-hidden','true');const path=document.createElementNS(mark.namespaceURI,'path');path.setAttribute('d','m4 10 4 4 8-8');mark.append(path);item.append(mark,node('span',c));capabilities.append(item);});host.append(capabilities);
}
async function calculate(event) {
  event?.preventDefault();if(busy||!form.reportValidity())return;
  busy=true;$('quick-calculate').disabled=true;form.querySelectorAll('input,select,button').forEach(el=>el.disabled=true);$('calculate').textContent='Calculando en Java…';$('form-error').hidden=true;$('quote-status').textContent='Calculando la composición…';renderLayers();
  const request={origin:$('origin').value,destination:$('destination').value,weightKg:Number($('weight').value),declaredValue:Number($('value').value),services:[...selected],profile:$('profile').value};
  try {
    const response=await fetch('/api/quotes',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(request),signal:AbortSignal.timeout(10000)});
    const data=await response.json();if(!response.ok)throw new Error(data.message||'La cotización no pudo calcularse. Revisa los datos.');
    latest=data;dirty=false;showResult(data);$('quote-status').textContent='Calculada por el servidor Java';$('quote-status').classList.remove('dirty');$('download').disabled=false;$('print').disabled=false;
  }catch(error){dirty=true;$('download').disabled=true;$('print').disabled=true;$('form-error').textContent=error.name==='TimeoutError'?'El servidor tardó demasiado. Vuelve a calcular.':error.message==='Failed to fetch'?'No hay conexión con el servidor Java. Comprueba que la aplicación siga abierta y vuelve a calcular.':error.message;$('form-error').hidden=false;$('quote-status').textContent='Revisa los datos y vuelve a calcular';}
  finally {busy=false;$('quick-calculate').disabled=false;form.querySelectorAll('input,select,button').forEach(el=>el.disabled=false);$('calculate').textContent='Calcular cotización';renderLayers();}
}
form.addEventListener('submit',calculate);
form.addEventListener('change',event=>{
 if(event.target.id==='preset')return;
 if(event.target.id==='profile')applyProfileRates();
 if(event.target.name==='services'){const id=event.target.value;if(event.target.checked)selected.push(id);else selected.splice(selected.indexOf(id),1);}
 $('preset').value='custom';changed();
});
form.addEventListener('input',event=>{if(event.target.type==='number'){$('preset').value='custom';changed();}});
$('preset').addEventListener('change',()=>{
 const option=$('preset').selectedOptions[0];if(!option||option.value==='custom')return;
 const preset={...option.dataset,services:option.dataset.services?option.dataset.services.split(','):[]};
 ['origin','destination','weight','value','profile'].forEach(key=>$(key).value=preset[key]);applyProfileRates();selected.splice(0,selected.length,...preset.services);form.querySelectorAll('input[name=services]').forEach(input=>input.checked=selected.includes(input.value));changed();calculate();
});
$('download').addEventListener('click',()=>{
 if(dirty||!latest)return;
 const params=new URLSearchParams({origin:latest.shipment.origin,destination:latest.shipment.destination,weightKg:latest.shipment.weightKg,declaredValue:latest.shipment.declaredValue,profile:latest.profile.id});
 latest.decorated.lines.filter(line=>line.id!=='base').forEach(line=>params.append('services',line.id));
 window.location.assign('/api/quotes/export?'+params.toString());
});
$('print').addEventListener('click',()=>{if(!dirty&&latest)window.print();});
$('compare').addEventListener('click',async()=>{
 if(busy||!form.reportValidity())return;
 const host=$('compare-result');const button=$('compare');button.disabled=true;$('form-error').hidden=true;
 const request={origin:$('origin').value,destination:$('destination').value,weightKg:Number($('weight').value),declaredValue:Number($('value').value),services:[...selected],profile:$('profile').value};
 try{
  const response=await fetch('/api/quotes/compare',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(request),signal:AbortSignal.timeout(10000)});
  const data=await response.json();if(!response.ok)throw new Error(data.message||'No se pudo comparar. Revisa los datos.');
  host.replaceChildren(node('div',`Desde ${request.origin} · mismo envío`,'currency'));
  data.forEach(row=>{const line=node('div',undefined,'quote-line');line.append(node('span',`${row.destination}${row.current?' (actual)':''} · ${row.deliveryHours} h`),node('span',money(row.total)));host.append(line);});
  host.hidden=false;
 }catch(error){host.hidden=true;$('form-error').textContent=error.name==='TimeoutError'?'El servidor tardó demasiado. Vuelve a intentarlo.':error.message==='Failed to fetch'?'No hay conexión con el servidor Java.':error.message;$('form-error').hidden=false;}
 finally{button.disabled=false;}
});
applyProfileRates();renderLayers();calculate();
