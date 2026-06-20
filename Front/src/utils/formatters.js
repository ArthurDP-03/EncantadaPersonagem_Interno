import i18n from "../i18n";

const getLocale = () => i18n.resolvedLanguage || i18n.language || "pt-BR";

export const formatarPeriodoEvento = (dataInicio, dataFim) => {
  const inicio = new Date(dataInicio);
  const fim = new Date(dataFim);
  const locale = getLocale();

  const data = new Intl.DateTimeFormat(locale, { dateStyle: "full" }).format(
    inicio,
  );
  const hora = new Intl.DateTimeFormat(locale, {
    hour: "2-digit",
    minute: "2-digit",
    hour12: false,
  }).format(inicio);

  const duracaoMs = fim - inicio;
  const duracaoHoras = Math.floor(duracaoMs / (1000 * 60 * 60));
  const duracaoMinutos = Math.floor(
    (duracaoMs % (1000 * 60 * 60)) / (1000 * 60),
  );

  const duracaoTexto =
    duracaoMinutos > 0
      ? i18n.t("events.duration.hoursMinutes", {
          hours: duracaoHoras,
          minutes: String(duracaoMinutos).padStart(2, "0"),
        })
      : i18n.t("events.duration.hours", { hours: duracaoHoras });

  return i18n.t("events.period", {
    date: data,
    time: hora,
    duration: duracaoTexto,
  });
};

export function labelData(data) {
  const hoje = new Date();
  hoje.setHours(0, 0, 0, 0);
  const d = new Date(data);
  d.setHours(0, 0, 0, 0);
  const diff = Math.round((d - hoje) / 86400000);

  const texto = new Intl.DateTimeFormat(getLocale(), {
    dateStyle: "full",
  }).format(d);

  if (diff === 0) return { prefixo: i18n.t("events.timeline.today"), texto };
  if (diff === -1)
    return { prefixo: i18n.t("events.timeline.yesterday"), texto };
  if (diff === 1) return { prefixo: i18n.t("events.timeline.tomorrow"), texto };
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
    RASCUNHO: i18n.t("events.statusLabels.draft"),
    CONFIRMADO: i18n.t("events.statusLabels.confirmed"),
    EM_ANDAMENTO: i18n.t("events.statusLabels.inProgress"),
    FINALIZADO: i18n.t("events.statusLabels.finished"),
    CANCELADO: i18n.t("events.statusLabels.cancelled"),
  };
  return labels[status] ?? status;
}
