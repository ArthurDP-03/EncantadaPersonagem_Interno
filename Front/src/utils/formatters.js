export const formatarPeriodoEvento = (dataInicio, dataFim) => {
  const inicio = new Date(dataInicio);
  const fim = new Date(dataFim);

  const diasSemana = ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"];
  const meses = ["jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez"];

  const diaSemana = diasSemana[inicio.getDay()];
  const dia = inicio.getDate();
  const mes = meses[inicio.getMonth()];
  const ano = inicio.getFullYear();
  const hora = inicio.getHours();

  const duracaoMs = fim - inicio;
  const duracaoHoras = Math.floor(duracaoMs / (1000 * 60 * 60));
  const duracaoMinutos = Math.floor((duracaoMs % (1000 * 60 * 60)) / (1000 * 60));

  const duracaoTexto = duracaoMinutos > 0 ? `${duracaoHoras}h${duracaoMinutos}min` : `${duracaoHoras}h`;

  return `${diaSemana}, ${dia} ${mes} ${ano} às ${hora}h - duração: ${duracaoTexto}`;
};

export function labelData(data) {
  const hoje = new Date();
  hoje.setHours(0, 0, 0, 0);
  const d = new Date(data);
  d.setHours(0, 0, 0, 0);
  const diff = Math.round((d - hoje) / 86400000);

  const semanas = ['Domingo','Segunda','Terça','Quarta','Quinta','Sexta','Sábado'];
  const meses = ['janeiro','fevereiro','março','abril','maio','junho',
                 'julho','agosto','setembro','outubro','novembro','dezembro'];
  const texto = `${semanas[d.getDay()]}, ${d.getDate()} de ${meses[d.getMonth()]} de ${d.getFullYear()}`;

  if (diff === 0)  return { prefixo: 'Hoje',   texto };
  if (diff === -1) return { prefixo: 'Ontem',  texto };
  if (diff === 1)  return { prefixo: 'Amanhã', texto };
  return { prefixo: null, texto };
}

export function agruparEventosPorData(eventos) {
  const grupos = {};
  eventos.forEach(ev => {
    const chave = ev.dataInicio.split('T')[0];
    if (!grupos[chave]) grupos[chave] = [];
    grupos[chave].push(ev);
  });

  const datas = Object.keys(grupos).sort();
  if (!datas.length) return [];

  return datas.map(chave => ({
    tipo: 'dia',
    data: new Date(chave + 'T00:00:00'),
    eventos: grupos[chave],
  }));
}

export function formatarStatus(status) {
  const labels = {
    RASCUNHO:    'Rascunho',
    CONFIRMADO:  'Confirmado',
    EM_ANDAMENTO: 'Em andamento',
    FINALIZADO:  'Finalizado',
    CANCELADO:   'Cancelado',
  };
  return labels[status] ?? status;
}