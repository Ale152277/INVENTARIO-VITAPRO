CREATE OR REPLACE FUNCTION public.reemplazar_bancos_equipo(
    p_equipo_id bigint,
    p_bancos jsonb
)
RETURNS void
LANGUAGE plpgsql
SECURITY INVOKER
SET search_path = ''
AS $$
BEGIN

    -- Verificar que el usuario sea administrador
    IF NOT EXISTS (
        SELECT 1
        FROM public.usuarios u
        WHERE u.auth_id = (SELECT auth.uid())
          AND u.rol::text = 'ADMIN'
          AND u.activo = true
    ) THEN
        RAISE EXCEPTION 'No autorizado para modificar bancos de baterías'
            USING ERRCODE = '42501';
    END IF;

    -- Verificar que recibimos una lista válida
    IF p_bancos IS NULL OR jsonb_typeof(p_bancos) <> 'array' THEN
        RAISE EXCEPTION 'Los bancos deben enviarse como un arreglo JSON';
    END IF;

    -- El formulario permite hasta tres bancos
    IF jsonb_array_length(p_bancos) > 3 THEN
        RAISE EXCEPTION 'Se permiten como máximo tres bancos';
    END IF;

    -- Verificar que el equipo exista
    IF NOT EXISTS (
        SELECT 1
        FROM public.equipos
        WHERE id = p_equipo_id
    ) THEN
        RAISE EXCEPTION 'El equipo no existe';
    END IF;

    -- Eliminar los bancos anteriores
    DELETE FROM public.equipos_banco_bateria
    WHERE equipo_id = p_equipo_id;

    -- Insertar los nuevos bancos
    INSERT INTO public.equipos_banco_bateria (
        equipo_id,
        numero_banco,
        bateria_voltaje,
        bateria_amperaje,
        bateria_cantidad
    )
    SELECT
        p_equipo_id,
        b.numero_banco,
        b.bateria_voltaje,
        b.bateria_amperaje,
        b.bateria_cantidad
    FROM jsonb_to_recordset(p_bancos) AS b(
        numero_banco integer,
        bateria_voltaje numeric,
        bateria_amperaje numeric,
        bateria_cantidad integer
    );

END;
$$;

-- Restringir quién puede ejecutar la función
REVOKE EXECUTE
ON FUNCTION public.reemplazar_bancos_equipo(bigint, jsonb)
FROM PUBLIC, anon;

GRANT EXECUTE
ON FUNCTION public.reemplazar_bancos_equipo(bigint, jsonb)
TO authenticated;