import { useState, useEffect } from "react";
import { getAdministrador } from "../services/administradoresService";

export function useAdministradores() {
  const [administradores, setAdministradores] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  useEffect(() => {
    getAdministrador()
        .then(setAdministradores)
        .catch(err => {
            console.error("Erro completo:", err);
            setErro(err.message);
        })
        .finally(() => setCarregando(false));
  }, []);

  return { administradores, carregando, erro };
}