package ch.heigvd.amt.wiki.dtos;

import java.util.Optional;
public record MediaWikiRecentChange(
        String title,
        String $schema,
        String type,
        boolean bot,
        String comment,
        Long id,
        Length length,
        String log_action,
        Optional<String> log_action_comment,
        Optional<Long> log_id,
        Object log_params,
        Optional<String> log_type,
        Meta meta,
        boolean minor,
        int namespace,
        String parsedcomment,
        boolean patrolled,
        Revision revision,
        String server_name,
        String server_script_path,
        String server_url,
        long timestamp,
        String user,
        String wiki,
        String notify_url
){}